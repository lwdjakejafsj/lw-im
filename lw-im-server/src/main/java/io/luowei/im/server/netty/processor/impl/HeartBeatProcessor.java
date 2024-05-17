package io.luowei.im.server.netty.processor.impl;

import cn.hutool.core.bean.BeanUtil;
import io.luowei.im.common.cache.distribute.DistributedCacheService;
import io.luowei.im.common.domain.constants.IMConstants;
import io.luowei.im.common.domain.enums.IMCmdType;
import io.luowei.im.common.domain.model.IMHeartBeatInfo;
import io.luowei.im.common.domain.model.IMSendInfo;
import io.luowei.im.server.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HeartBeatProcessor implements MessageProcessor {
    @Autowired
    private DistributedCacheService distributedCacheService;

    @Value("${heartbeat.count}")
    private Integer heartbeatCount;


    @Override
    public void process(ChannelHandlerContext ctx, Object data) {
        // 响应
        this.responseWS(ctx);

        AttributeKey<Long> heartBeatAttr = AttributeKey.valueOf(IMConstants.HEARTBEAT_TIMES);
        Long count = ctx.channel().attr(heartBeatAttr).get();
        ctx.channel().attr(heartBeatAttr).set(++count);

        if (count % heartbeatCount == 0) {
            //心跳10次，用户在线状态续命一次
            AttributeKey<Long> userIdAttr = AttributeKey.valueOf(IMConstants.USER_ID);
            Long userId = ctx.channel().attr(userIdAttr).get();
            AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
            Integer terminal = ctx.channel().attr(terminalAttr).get();
            String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, userId.toString(), terminal.toString());
            distributedCacheService.expire(redisKey, IMConstants.ONLINE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        }
    }


    /**
     * 响应ws的数据
     */
    private void responseWS(ChannelHandlerContext ctx) {
        // 响应WS的数据
        IMSendInfo<?> imSendInfo = new IMSendInfo<>();
        imSendInfo.setCmd(IMCmdType.HEART_BEAT.code());
        ctx.channel().writeAndFlush(imSendInfo);
    }

    @Override
    public IMHeartBeatInfo transForm(Object obj) {
        Map<?, ?> map = (Map<?, ?>) obj;
        return BeanUtil.fillBeanWithMap(map, new IMHeartBeatInfo(), false);
    }
}
