package io.luowei.im.server.consumer;

import cn.hutool.core.util.StrUtil;
import io.luowei.im.common.domain.constants.IMConstants;
import io.luowei.im.common.domain.enums.IMCmdType;
import io.luowei.im.common.domain.model.IMReceiveInfo;
import io.luowei.im.server.netty.processor.MessageProcessor;
import io.luowei.im.server.netty.processor.factory.ProcessorFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 *
 * 该类是消费消息中间件中单聊消息的消费者
 * author: luowei
 * date:
 */
@Component
@ConditionalOnProperty(prefix = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = IMConstants.IM_MESSAGE_PRIVATE_CONSUMER_GROUP,
                         topic = IMConstants.IM_MESSAGE_PRIVATE_NULL_QUEUE)
public class PrivateMessageConsumer extends BaseMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private final Logger logger = LoggerFactory.getLogger(PrivateMessageConsumer.class);

    @Value("${server.id}")
    private Long serverId;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)){
            logger.warn("PrivateMessageConsumer.onMessage|接收到的消息为空");
            return;
        }
        IMReceiveInfo receiveInfo = this.getReceiveInfo(message);
        if (receiveInfo == null){
            logger.warn("PrivateMessageConsumer.onMessage|转化后的数据为空");
            return;
        }
        MessageProcessor processor = ProcessorFactory.getProcessor(IMCmdType.PRIVATE_MESSAGE);
        processor.process(receiveInfo);
    }

    // 动态监听当前服务对应的RocketMq的topic
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try{
            String topic = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_PRIVATE_QUEUE, String.valueOf(serverId));
            consumer.subscribe(topic, "*");
        }catch (Exception e){
            logger.error("PrivateMessageConsumer.prepareStart|异常:{}", e.getMessage());
        }
    }
}
