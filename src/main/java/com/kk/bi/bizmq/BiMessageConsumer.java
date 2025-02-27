package com.kk.bi.bizmq;

import com.kk.bi.common.ErrorCode;
import com.kk.bi.common.TaskStatus;
import com.kk.bi.exception.BusinessException;
import com.kk.bi.manager.AIManager;
import com.kk.bi.model.entity.Chart;
import com.kk.bi.service.ChartService;
import com.kk.bi.utils.ExcelUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static com.kk.bi.bizmq.BiMqConstant.BI_QUEUE_NAME;

/**
 * 业务消息消费者
 *
 * @author : LXRkk
 * @date : 2025/2/15 21:10
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AIManager aiManager;

    // 指定程序监听的消息队列和确认机制
    @RabbitListener(queues = {BI_QUEUE_NAME}, ackMode = "MANUAL") // 自动给方法传入相应参数
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("传入的图表 id：{}", message);
        if (StringUtils.isBlank(message)) {
            // 如果失败，消息拒绝
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空！");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表不存在！");
        }
        // 修改图表任务状态为 ”执行中“，执行成功后，修改为 ”已完成“；执行失败修改为 ”失败“，记录任务信息状态
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(TaskStatus.EXECUTING.getMessage());
        boolean updateRunning = chartService.updateById(updateChart);
        if (!updateRunning) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            return;
        }
        // 调用 AI
        String aiAnswer = aiManager.sendMsgToAI(true, buildUserInput(chart));
        String[] splits = aiAnswer.split("```");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }
        // 生成的图表代码
        String genChart = splits[1].trim();
        // 生成的分析结论
        String genResult = splits[2].trim();
        Chart updateSuccessChart = new Chart();
        updateSuccessChart.setId(chart.getId());
        updateSuccessChart.setGenChart(genChart);
        updateSuccessChart.setGenResult(genResult);
        updateSuccessChart.setStatus(TaskStatus.SUCCEED.getMessage());
        boolean result = chartService.updateById(updateSuccessChart);
        if (!result) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(), "更新图表生成成功状态失败");
        }
        log.info("received message : {}", message);
        try {
            // 手动确认
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            log.error("confirm message failed:{}",e.getMessage());
        }
    }

    /**
     * 构造用户输入
     * @param chart
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求:").append("\n");
        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据:").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }

    private void handleChartUpdateError(long chartId, String errorMessage) {
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setStatus(TaskStatus.FAILED.getMessage());
        log.error("这里发生错误！");
        boolean result = chartService.updateById(chart);
        if (!result) {
            log.error("更新图表失败状态失败" + chartId + "," + errorMessage);
        }

    }
}
