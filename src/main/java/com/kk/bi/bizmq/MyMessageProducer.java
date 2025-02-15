package com.kk.bi.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 业务消息生产者
 *
 * @author : LXRkk
 * @date : 2025/2/15 21:10
 */
@Component
public class MyMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
