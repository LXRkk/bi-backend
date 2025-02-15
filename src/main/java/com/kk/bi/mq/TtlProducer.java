package com.kk.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class TtlProducer {

    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.159.0.101");
        factory.setUsername("root");
        factory.setPassword("123456");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}