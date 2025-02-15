package com.kk.bi.bizmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import static com.kk.bi.bizmq.BiMqConstant.*;

/**
 * 该类用作创建业务交换机和队列，只在程序执行前启动一次
 *
 * @author : LXRkk
 * @date : 2025/2/15 21:34
 */
public class BiInitMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(MQ_HOST);
        connectionFactory.setUsername(MQ_USER_NAME);
        connectionFactory.setPassword(MQ_PASSSWORD);
        try {
            // 创建连接
            Connection connection = connectionFactory.newConnection();
            // 创建频道
            Channel channel = connection.createChannel();
            // 声明交换机
            channel.exchangeDeclare(BI_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            // 声明队列
            channel.queueDeclare(BI_QUEUE_NAME,true,false,false,null);
            // 队列绑定交换机
            channel.queueBind(BI_QUEUE_NAME,BI_EXCHANGE_NAME,BI_ROUTINGK_KEY);

        } catch (Exception e) {
        }
    }
}
