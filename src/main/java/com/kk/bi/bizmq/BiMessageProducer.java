package com.kk.bi.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.kk.bi.bizmq.BiMqConstant.BI_EXCHANGE_NAME;
import static com.kk.bi.bizmq.BiMqConstant.BI_ROUTINGK_KEY;

/**
 * 业务消息生产者
 *
 * @author : LXRkk
 * @date : 2025/2/15 21:10
 */
@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(BI_EXCHANGE_NAME, BI_ROUTINGK_KEY, message);
    }
}
