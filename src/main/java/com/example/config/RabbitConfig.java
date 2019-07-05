package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuandf
 * @description
 * @date 2019/7/4
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        /**
         * 实现消息发送到RabbitMQ交换器后接收ack回调,如果消息发送确认失败就进行重试.
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (null != correlationData) {
                if (ack) {
                    log.info("消息发送成功,消息ID:{}", correlationData.getId());
                } else {
                    log.info("消息发送失败，消息ID:{}", correlationData.getId());
                }
            }
        });
        /**
         * 实现消息发送到RabbitMQ交换器,但无相应队列与交换器绑定时的回调.
         */
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> log.error("消息发送失败，replyCode:{}, replyText:{}，exchange:{}，routingKey:{}，消息体:{}", replyCode, replyText, exchange, routingKey, new String(message.getBody())));
        return rabbitTemplate;
    }


    /**
     * 1.队列名字
     * 2.durable="true" 是否持久化 rabbitmq重启的时候不需要创建新的队列
     * 3.auto-delete    表示消息队列没有在使用时将被自动删除 默认是false
     * 4.exclusive      表示该消息队列是否只在当前connection生效,默认是false
     */
    @Bean("sendSystemMessageQueue")
    public Queue sendSystemMessageQueue() {
        // 参数依次为：队列名，是否持久化，是否排他，是否自动删除
        return new Queue(MQConstant.QUEUE_SYSTEM_MESSAGE, true, false, false);
    }

    @Bean("processApprovalQueue")
    public Queue processApprovalQueue() {
        // 参数依次为：队列名，是否持久化，是否排他，是否自动删除
        return new Queue(MQConstant.QUEUE_PROCESS_APPROVAL, true, false, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        // 参数依次是：交换器名称，是否持久化，是否自动删除
        return new TopicExchange(MQConstant.EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindingTopic() {
        return BindingBuilder.bind(sendSystemMessageQueue()).to(topicExchange()).with(MQConstant.ROUTING_KEY_WORKFLOW_MESSAGE);
    }
}
