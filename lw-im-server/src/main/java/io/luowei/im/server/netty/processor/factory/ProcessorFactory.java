package io.luowei.im.server.netty.processor.factory;

import io.luowei.im.common.domain.enums.IMCmdType;
import io.luowei.im.server.holder.SpringContextHolder;
import io.luowei.im.server.netty.processor.MessageProcessor;
import io.luowei.im.server.netty.processor.impl.GroupMessageProcessor;
import io.luowei.im.server.netty.processor.impl.HeartBeatProcessor;
import io.luowei.im.server.netty.processor.impl.LoginProcessor;
import io.luowei.im.server.netty.processor.impl.PrivateMessageProcessor;

/**
 * author luowei
 * description 处理器工厂类
 */
public class ProcessorFactory {

    public static MessageProcessor<?> getProcessor(IMCmdType cmd){
        switch (cmd){
            //登录
            case LOGIN:
                return SpringContextHolder.getApplicationContext().getBean(LoginProcessor.class);
            //心跳
            case HEART_BEAT:
                return SpringContextHolder.getApplicationContext().getBean(HeartBeatProcessor.class);
            //单聊消息
            case PRIVATE_MESSAGE:
                return SpringContextHolder.getApplicationContext().getBean(PrivateMessageProcessor.class);
            //群聊消息
            case GROUP_MESSAGE:
                return SpringContextHolder.getApplicationContext().getBean(GroupMessageProcessor.class);
            default:
                return null;

        }
    }
}