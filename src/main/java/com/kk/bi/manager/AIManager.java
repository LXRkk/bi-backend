package com.kk.bi.manager;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用 AI 服务
 *
 * @author : LXRkk
 * @date : 2025/1/23 21:39
 */
@Service
@Slf4j
public class AIManager {

    @Resource
    private SparkClient sparkClient;

    final String SYSTEM_PROMPT = "你是一名数据分析师和高级前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
            "分析需求：\n" +
            "{这里是我的分析需求}\n" +
            "原始数据：\n" +
            "{这里是csv格式的原始数据，用,分隔开了}\n" +
            "\n" +
            "请根据我提供的分析需求和原始数据，，严格按照以下格式生成内容（不要生成任何多余的开头、结尾和注释）：\n" +
            "```\n" +
            "{前端 Echarts V5 的 option 配置对象 JSON 代码, 不要生成任何多余的内容，比如注释和代码块标记}\n" +
            "```\n" +
            "这里是根据数据得出的结论，输出格式为文本，越详细越好，不要生成其他内容\n" +
            "\n" +
            "输出示例：\n" +
            "```\n" +
            "{\n" +
            "  \"tooltip\": {\n" +
            "    \"trigger\": \"axis\"\n" +
            "  },\n" +
            "  \"legend\": {\n" +
            "    \"data\": [\"用户数\"]\n" +
            "  },\n" +
            "  \"xAxis\": {\n" +
            "    \"type\": \"category\",\n" +
            "    \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\"]\n" +
            "  },\n" +
            "  \"yAxis\": {\n" +
            "    \"type\": \"value\"\n" +
            "  },\n" +
            "  \"series\": [\n" +
            "    {\n" +
            "      \"name\": \"用户数\",\n" +
            "      \"type\": \"line\",\n" +
            "      \"data\": [10, 20, 30, 5, 15, 3]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "```\n" +
            "从提供的数据来看，网站用户的增长情况呈现出一定的波动性。在1号到3号期间，用户数呈现显著增长，从10人增加到30人。然而，在4号时用户数急剧下降至5人，然后在5号有所回升至15人，但在6号又再次下降至3人。整体上，虽然前期有增长趋势，但后期的波动较大，需要进一步分析原因并采取措施稳定用户增长。";




    /**
     * 向 AI 发送请求
     *
     * @param isNeedTemplate 是否使用模板，进行 AI 生成； true 使用 、false 不使用 ，false 的情况是只想用 AI 不只是生成前端代码
     * @param content        内容
     *                       分析需求：
     *                       分析网站用户的增长情况
     *                       原始数据：
     *                       日期,用户数
     *                       1号,10
     *                       2号,20
     *                       3号,30
     * @return AI 返回的内容
     * '【【【【【'
     * <p>
     * '【【【【【'
     */
    public String sendMsgToAI(boolean isNeedTemplate, String content) {
        if (isNeedTemplate) {
            // AI 生成问题的预设条件
            /*String predefinedInformation = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                    "分析需求：\n" +
                    "{数据分析的需求或者目标}\n" +
                    "原始数据：\n" +
                    "{csv格式的原始数据，用,作为分隔符}\n" +
                    "请根据这两部分内容，严格按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）同时不要使用这个符号 '】'\n" +
                    "'【【【【【'\n" +
                    "{前端 Echarts V5 的 option 配置对象 JSON 代码, 不要生成任何多余的内容，比如注释和代码块标记}\n" +
                    "'【【【【【'\n" +
                    "{明确的数据分析结论、越详细越好，不要生成多余的注释} \n"
                    + "下面是一个具体的例子的模板："
                    + "'【【【【【'\n"
                    + "JSON格式代码"
                    + "'【【【【【'\n" +
                    "结论：";*/
            content = SYSTEM_PROMPT + "\n" + content;
        }
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.userContent(content));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传,取值为[1,4096],默认为2048
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本
                .apiVersion(SparkApiVersion.V4_0)
                .build();
        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        String responseContent = chatResponse.getContent();
        log.info(responseContent);
        return responseContent;
    }
}