package com.kk.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class TopicConsumer {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.159.0.101");
        factory.setUsername("root");
        factory.setPassword("123456");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        // 创建队列
        String queueName = "xiaoa_queue";
        channel.queueDeclare(queueName,true,false,false,null);
        channel.queueBind(queueName,EXCHANGE_NAME,"#.前端.#");

        // 创建队列
        String queueName2 = "xiaob_queue";
        channel.queueDeclare(queueName2,true,false,false,null);
        channel.queueBind(queueName2,EXCHANGE_NAME,"#.后端.#");

        // 创建队列
        String queueName3 = "xiaoc_queue";
        channel.queueDeclare(queueName3,true,false,false,null);
        channel.queueBind(queueName3,EXCHANGE_NAME,"#.产品.#");


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback xiaoadeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoa] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback xiaobdeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaob] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback xiaocdeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoc] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueName, true, xiaoadeliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName2, true, xiaobdeliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName3, true, xiaocdeliverCallback, consumerTag -> {
        });
    }
}