package io.luowei.im.server.netty.processor.impl;

import cn.hutool.core.bean.BeanUtil;
import io.luowei.im.common.domain.enums.IMCmdType;
import io.luowei.im.common.domain.enums.IMSendCode;
import io.luowei.im.common.domain.model.IMHeartBeatInfo;
import io.luowei.im.common.domain.model.IMReceiveInfo;
import io.luowei.im.common.domain.model.IMSendInfo;
import io.luowei.im.common.domain.model.IMUserInfo;
import io.luowei.im.common.mq.MessageSenderService;
import io.luowei.im.server.consumer.BaseMessageConsumer;
import io.luowei.im.server.netty.cache.UserChannelContextCache;
import io.luowei.im.server.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class PrivateMessageProcessor extends BaseMessageProcessor implements MessageProcessor<IMReceiveInfo> {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageProcessor.class);

    @Autowired
    private MessageSenderService messageSenderService;

    @Override
    public void process(IMReceiveInfo receiveInfo) {

        IMUserInfo sender = receiveInfo.getSender();
        IMUserInfo receiver = receiveInfo.getReceivers().get(0);
        logger.info("PrivateMessageProcessor.process|接收到消息,发送者:{}, 接收者:{}, 内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());
        try{
            ChannelHandlerContext channelHandlerContext = UserChannelContextCache
                    .getChannelCtx(receiver.getUserId(), receiver.getTerminal());
            if (channelHandlerContext != null){
                //推送消息
                IMSendInfo<?> imSendInfo = new IMSendInfo<>(IMCmdType.PRIVATE_MESSAGE.code(), receiveInfo.getData());
                channelHandlerContext.writeAndFlush(imSendInfo);
                sendPrivateMessageResult(receiveInfo, IMSendCode.SUCCESS);
            }else{
                sendPrivateMessageResult(receiveInfo, IMSendCode.NOT_FIND_CHANNEL);
                logger.error("PrivateMessageProcessor.process|未找到Channel, 发送者:{}, 接收者:{}, 内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());
            }
        }catch (Exception e){
            sendPrivateMessageResult(receiveInfo, IMSendCode.UNKNOWN_ERROR);
            logger.error("PrivateMessageProcessor.process|发送异常,发送者:{}, 接收者:{}, 内容:{}, 异常信息:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData(), e.getMessage());
        }

    }

}
