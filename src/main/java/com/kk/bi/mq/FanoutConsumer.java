package com.kk.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanoutConsumer {
  private static final String EXCHANGE_NAME = "fanout-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("10.159.0.101");
    factory.setUsername("root");
    factory.setPassword("123456");
    Connection connection = factory.newConnection();
    Channel channel1 = connection.createChannel();
    Channel channel2 = connection.createChannel();

    channel1.exchangeDeclare(EXCHANGE_NAME, "fanout");
    channel2.exchangeDeclare(EXCHANGE_NAME, "fanout");

    String queueName1 = "xiaowang";
    channel1.queueDeclare(queueName1,true,false,false,null);
    channel1.queueBind(queueName1, EXCHANGE_NAME, "");

    String queueName2 = "xiaoli";
    channel2.queueDeclare(queueName2,true,false,false,null);
    channel2.queueBind(queueName2, EXCHANGE_NAME, "");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [" + queueName1 + "] Received '" + message + "'");
    };
    DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      System.out.println(" [" + queueName2 + "] Received '" + message + "'");
    };
    channel1.basicConsume(queueName1, true, deliverCallback1, consumerTag -> { });
    channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
  }
}