/**
 * Copyright 2022-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * author luowei
 * description 消息消费者
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = IMConstants.IM_MESSAGE_GROUP_CONSUMER_GROUP, topic = IMConstants.IM_MESSAGE_GROUP_NULL_QUEUE)
public class GroupMessageConsumer extends BaseMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageConsumer.class);

    @Value("${server.id}")
    private Long serverId;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)){
            logger.warn("GroupMessageConsumer.onMessage|接收到的消息为空");
            return;
        }
        IMReceiveInfo imReceiveInfo = this.getReceiveInfo(message);
        if (imReceiveInfo == null){
            logger.warn("GroupMessageConsumer.onMessage|转化后的数据为空");
            return;
        }
        MessageProcessor processor = ProcessorFactory.getProcessor(IMCmdType.GROUP_MESSAGE);
        processor.process(imReceiveInfo);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try{
            String topic = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_GROUP_QUEUE, String.valueOf(serverId));
            consumer.subscribe(topic, "*");
        }catch (Exception e){
            logger.error("GroupMessageConsumer.prepareStart|异常:{}", e.getMessage());
        }
    }
}
