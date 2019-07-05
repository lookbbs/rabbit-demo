package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class RabbitSender {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {

    }


    /**
     * convertAndSend 异步,消息是否发送成功用ConfirmCallback和ReturnCallback回调函数类确认。
     * 发送MQ消息
     */
    public void sendMessage(String exchangeName, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, new CorrelationData(UUID.randomUUID().toString()));
    }

    /**
     * sendMessageAndReturn 当发送消息过后,该方法会一直阻塞在哪里等待返回结果,直到请求超时,配置spring.rabbitmq.template.reply-timeout来配置超时时间。
     * 发送MQ消息并返回结果
     */
    public Object sendMessageAndReturn(String exchangeName, String routingKey, Object message) {
        return rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, message, new CorrelationData(UUID.randomUUID().toString()));
    }

    public void sendDelay(String exchangeName, String routingKey, Order order, long expireTime) {
        log.info("【订单生成时间】" + new Date().toString() + "【1分钟后检查订单是否已经支付】" + order.toString());
        this.rabbitTemplate.convertAndSend(exchangeName, routingKey, order, message -> {
            message.getMessageProperties().setContentEncoding("UTF-8");
            // 如果配置了 params.put("x-message-ttl", 5 * 1000); 那么这一句也可以省略,具体根据业务需要是声明 Queue 的时候就指定好延迟时间还是在发送自己控制时间
            message.getMessageProperties().setExpiration(expireTime + "");
            return message;
        });
    }

}