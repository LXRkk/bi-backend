package com.kk.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class DlxDirectProducer {

  private static final String NORMAL_EXCHANGE_NAME = "normal_direct_exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("10.159.0.101");
      factory.setUsername("root");
      factory.setPassword("123456");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        // 声明正常交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE_NAME, "direct");


        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String userInput = scanner.nextLine();
            String[] splits = userInput.split(" ");
            if (splits.length < 2) {
                continue;
            }
            String message = splits[0];
            String routingKey = splits[1];

            channel.basicPublish(NORMAL_EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'with routingKey:'" + routingKey + "'");
        }

    }
  }
  //..
}