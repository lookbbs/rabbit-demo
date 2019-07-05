package com.example;

import com.example.config.DelayRabbitConfig;
import com.example.config.MQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author yuandf
 * @description
 * @date 2019/7/5
 */
@Slf4j
@RestController
public class MessageProducer {

    @Autowired
    private RabbitSender rabbitSender;

    @GetMapping("/{length:\\d+}")
    public void send(@PathVariable int length) {
        for (int i = 1; i <= length; i++) {
            String message = String.format("消息记录：%03d", i);
            log.info(">>> 待发送的消息：{}", message);
            Message build = MessageBuilder.withBody(message.getBytes()).setAppId(UUID.randomUUID().toString()).build();
            rabbitSender.sendMessage(MQConstant.EXCHANGE_NAME, MQConstant.ROUTING_KEY_WORKFLOW_MESSAGE, build);
        }
    }

    @GetMapping("/delay/{length:\\d+}")
    public void sendDelay(@PathVariable int length) {
        Order order1 = new Order();
        order1.setOrderStatus(0);
        order1.setOrderId("123456");
        order1.setOrderName("小米6");

        Order order2 = new Order();
        order2.setOrderStatus(1);
        order2.setOrderId("456789");
        order2.setOrderName("小米8");
        long t = 1 * 60 * 1000;
        rabbitSender.sendDelay(DelayRabbitConfig.ORDER_DELAY_EXCHANGE, DelayRabbitConfig.ORDER_DELAY_ROUTING_KEY, order1, t);
        rabbitSender.sendDelay(DelayRabbitConfig.ORDER_DELAY_EXCHANGE, DelayRabbitConfig.ORDER_DELAY_ROUTING_KEY, order2, t);
    }
}
