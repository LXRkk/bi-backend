package com.kk.bi.mq;

import com.rabbitmq.client.*;

import java.util.HashMap;
import java.util.Map;

public class DlxDirectConsumer {

    private static final String NORMAL_EXCHANGE_NAME = "normal_direct_exchange";
    private static final String DEAD_EXCHANGE_NAME = "dead_direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.159.0.101");
        factory.setUsername("root");
        factory.setPassword("123456");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 声明死信队列
        String queueName = "boss_dlx_queue";
        channel.queueDeclare(queueName,true,false,false,null);
        // 死信队列绑定死信交换机
        channel.queueBind(queueName,DEAD_EXCHANGE_NAME,"laoban");

        // 声明死信队列
        String queueName2 = "waibao_dlx_queue";
        channel.queueDeclare(queueName2,true,false,false,null);
        // 死信队列绑定死信交换机
        channel.queueBind(queueName2,DEAD_EXCHANGE_NAME,"waibao");

        // 正常队列绑定死信队列信息
        Map<String,Object> args1 = new HashMap<>();
        args1.put("x-dead-letter-exchange",DEAD_EXCHANGE_NAME);
        args1.put("x-dead-letter-routing-key", "laoban");

        // 正常队列绑定死信队列信息
        Map<String,Object> args2 = new HashMap<>();
        args2.put("x-dead-letter-exchange",DEAD_EXCHANGE_NAME);
        args2.put("x-dead-letter-routing-key", "waibao");

        // 声明正常交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 声明正常队列
        String queueName3 = "xiaocat_queue";
        channel.queueDeclare(queueName3, true, false, false, args1);
        channel.queueBind(queueName3, NORMAL_EXCHANGE_NAME, "xiaocat");

        // 声明正常队列
        String queueName4 = "xiaodog_queue";
        channel.queueDeclare(queueName4, true, false, false, args2);
        channel.queueBind(queueName4, NORMAL_EXCHANGE_NAME, "xiaodog");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 消费普通消息回调函数
        DeliverCallback xiaocatdeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 拒绝消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            System.out.println(" [xiaocat] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        // 消费普通消息回调函数
        DeliverCallback xiaodogdeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 拒绝消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            System.out.println(" [xiaodog] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueName3, false, xiaocatdeliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName4, false, xiaodogdeliverCallback, consumerTag -> {
        });

        // 消费死信消息回调函数
        DeliverCallback laobandeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            System.out.println(" [laoban] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback waibaodeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            System.out.println(" [waibao] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueName,false,laobandeliverCallback, consumerTag -> {});
        channel.basicConsume(queueName2,false,waibaodeliverCallback, consumerTag -> {});
    }
}