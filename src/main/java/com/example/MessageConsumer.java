package com.example;

import com.example.config.DelayRabbitConfig;
import com.example.config.MQConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author yuandf
 * @description
 * @date 2019/7/5
 */
@Slf4j
@Component
public class MessageConsumer {

    @RabbitListener(queues = MQConstant.QUEUE_SYSTEM_MESSAGE)
    public void process(Message message, Channel channel) throws IOException {
        log.info(">>> 收到消息：{}", new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }

    @RabbitListener(queues = {DelayRabbitConfig.ORDER_QUEUE_NAME})
    public void delayQueue(Order order, Message message, Channel channel) throws IOException {
        log.info("###########################################");
        log.info("【orderDelayQueue 监听的消息】 - 【消费时间】 - [{}]- 【订单内容】 - [{}]", new Date(), order.toString());
        if (order.getOrderStatus() == 0) {
            order.setOrderStatus(2);
            log.info("【该订单未支付，取消订单】" + order.toString());
        } else if (order.getOrderStatus() == 1) {
            log.info("【该订单已完成支付】");
        } else if (order.getOrderStatus() == 2) {
            log.info("【该订单已取消】");
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.info("###########################################");
    }
}
