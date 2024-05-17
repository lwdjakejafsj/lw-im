package io.luowei.im.common.mq.event;

import io.luowei.im.common.domain.model.TopicMessage;
import org.apache.rocketmq.client.producer.TransactionSendResult;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description 服务事件发布器
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
public interface MessageEventSenderService {

    /**
     * 发送消息
     * @param message 发送的消息
     */
    boolean send(TopicMessage message);

    /**
     * 发送事务消息，主要是RocketMQ
     * @param message 事务消息
     * @param arg 其他参数
     * @return 返回事务发送结果
     */
    default TransactionSendResult sendMessageInTransaction(TopicMessage message, Object arg){
        return null;
    }
}
