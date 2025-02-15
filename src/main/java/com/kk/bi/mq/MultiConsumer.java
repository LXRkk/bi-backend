package com.kk.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.159.0.101");
        factory.setUsername("root");
        factory.setPassword("123456");
        final Connection connection = factory.newConnection();

        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();

            // 队列持久化
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


            channel.basicQos(1);
            // 定义如何处理消息
            int fianlI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                System.out.println(" [x] Received '" + "编号" + fianlI + ":" + message + "'");
                try {
                    // 睡眠 20s，模拟机器处理能力有限
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println(" [x] Done");
                    // 单个应答
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            // 手动应答，开启消费
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }

}