package io.luowei.im.server.netty.cache;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用来缓存 channel映射
 * author: luowei
 * date:
 */
public class UserChannelContextCache {

    // Map<userId , Map<终端类型 , channel> >
    private static Map<Long,ConcurrentHashMap<Integer, ChannelHandlerContext>> channelMap
                                = new ConcurrentHashMap<>();

    public static void addChannelCtx(Long userId , Integer terminal , ChannelHandlerContext ctx) {
//        channelMap.computeIfAbsent(userId, key -> new ConcurrentHashMap<>()).put(terminal,ctx);
        Map<Integer, ChannelHandlerContext> map = channelMap
                .computeIfAbsent(userId, key -> new ConcurrentHashMap<>());

        ChannelHandlerContext context = map.get(terminal);
        if (context != null) {
            context.channel().close();
        }

        map.put(terminal,ctx);

    }

    public static void removeChannelCtx(Long userId, Integer terminal){
        if (userId != null && terminal != null && channelMap.containsKey(userId)){
            Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
            if (userChannelMap.containsKey(terminal)){
                userChannelMap.remove(terminal);
            }
        }
    }

    public static ChannelHandlerContext getChannelCtx(Long userId, Integer terminal){
        if (userId != null && terminal != null && channelMap.containsKey(userId)){
            Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
            if (userChannelMap.containsKey(terminal)){
                return userChannelMap.get(terminal);
            }
        }
        return null;
    }

    public static Map<Integer, ChannelHandlerContext> getChannelCtx(Long userId){
        if (userId == null){
            return null;
        }
        return channelMap.get(userId);
    }
}
