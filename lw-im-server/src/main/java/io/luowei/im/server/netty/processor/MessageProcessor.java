package io.luowei.im.server.netty.processor;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理器接口，用来处理不同的消息
 * author: luowei
 * date:
 */
public interface MessageProcessor<T> {

    /**
     * 处理数据
     */
    default void process(ChannelHandlerContext ctx, T data){

    }

    /**
     * 处理数据
     */
    default void process(T data){

    }

    /**
     * 转化数据
     */
    default T transForm(Object obj){
        return (T) obj;
    }

}
