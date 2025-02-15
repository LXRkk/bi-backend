package com.kk.bi.mq;

import com.rabbitmq.client.*;

public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.159.0.101");
        factory.setUsername("root");
        factory.setPassword("123456");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = "xiaoqi_queue";
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "xiaoqi");

        String queueName2 = "shisan_queue";
        channel.queueDeclare(queueName2, true, false, false, null);
        channel.queueBind(queueName2, EXCHANGE_NAME, "shisan");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback xiaoqideliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoqi] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback shisandeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [shisan] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueName, true, xiaoqideliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName2, true, shisandeliverCallback, consumerTag -> {
        });
    }
}