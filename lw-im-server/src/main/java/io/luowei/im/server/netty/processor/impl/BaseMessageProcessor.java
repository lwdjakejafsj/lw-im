package io.luowei.im.server.netty.processor.impl;

import io.luowei.im.common.domain.constants.IMConstants;
import io.luowei.im.common.domain.enums.IMSendCode;
import io.luowei.im.common.domain.model.IMReceiveInfo;
import io.luowei.im.common.domain.model.IMSendResult;
import io.luowei.im.common.domain.model.IMUserInfo;
import io.luowei.im.common.mq.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseMessageProcessor {
    @Autowired
    private MessageSenderService messageSenderService;

    protected void sendPrivateMessageResult(IMReceiveInfo receiveInfo, IMSendCode sendCode){
        if (receiveInfo.getSendResult()) {
            IMSendResult result = new IMSendResult(receiveInfo.getSender()
                                                , receiveInfo.getReceivers().get(0)
                                                , receiveInfo.getCmd()
                                                , receiveInfo.getData());
            String sendKey = IMConstants.IM_RESULT_PRIVATE_QUEUE;
            result.setDestination(sendKey);
            messageSenderService.send(result);
        }
    }

    /**
     * 发送结果数据
     */
    protected void sendGroupMessageResult(IMReceiveInfo imReceiveInfo, IMUserInfo imUserInfo, IMSendCode imSendCode){
        if (imReceiveInfo.getSendResult()){
            IMSendResult<?> result = new IMSendResult<>(imReceiveInfo.getSender(), imUserInfo, imSendCode.code(), imReceiveInfo.getData());
            String sendKey = IMConstants.IM_RESULT_GROUP_QUEUE;
            result.setDestination(sendKey);
            messageSenderService.send(result);
        }
    }

}
