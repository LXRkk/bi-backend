package com.kk.bi.bizmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 该类用作创建测试交换机和队列，只在程序执行前启动一次
 *
 * @author : LXRkk
 * @date : 2025/2/15 21:34
 */
public class MqInitMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("10.159.0.101");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("123456");
        try {
            // 创建连接
            Connection connection = connectionFactory.newConnection();
            // 创建频道
            Channel channel = connection.createChannel();
            // 声明交换机
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            // 声明队列
            String QUEUE_NAME = "code_queue";
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            // 队列绑定交换机
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"my_routingKey");

        } catch (Exception e) {
        }
    }
}
