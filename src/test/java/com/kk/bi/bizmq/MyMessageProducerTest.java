package com.kk.bi.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private MyMessageProducer myMessageProducer;
    @Test
    void sendMessage() {
/*        myMessageProducer.sendMessage("code_exchange",
                "my_routingKey",
                "Spring Boot 整合 RabbitMQ 测试");*/
        //错误路由测试，测试结果：接收不到消息
        myMessageProducer.sendMessage("code_exchange",
                "my_routing",
                "Spring Boot 整合 RabbitMQ 测试");
    }
}