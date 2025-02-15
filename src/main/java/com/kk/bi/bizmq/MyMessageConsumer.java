package com.kk.bi.bizmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 业务消息消费者
 *
 * @author : LXRkk
 * @date : 2025/2/15 21:10
 */
@Component
@Slf4j
public class MyMessageConsumer {

    // 指定程序监听的消息队列和确认机制
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL") // 自动给方法传入相应参数
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("received message : {}", message);
        try {
            // 手动确认
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            log.error("confirm message failed:{}",e.getMessage());
        }
    }
}
