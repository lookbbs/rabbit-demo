package com.example.config;

/**
 * @author yuandf
 * @description
 * @date 2019/7/5
 */
public interface MQConstant {
    /**
     * Exchange
     */
    String EXCHANGE_NAME = "workflow.exchange";
    /**
     * 服务名.业务类型.动作名.queue
     * 发送系统消息
     */
    String QUEUE_SYSTEM_MESSAGE = "workflow.system_message.send.queue";

    /**
     * 流程审批
     */
    String QUEUE_PROCESS_APPROVAL = "workflow.process.approval.queue";

    /**
     * 所有匹配workflow.system_message.# key的queue都绑定到topic交换器上（订阅）
     */
    String ROUTING_KEY_WORKFLOW_MESSAGE = "workflow.system_message.#";
}
