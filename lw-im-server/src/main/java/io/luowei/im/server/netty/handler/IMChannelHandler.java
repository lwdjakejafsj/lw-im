package io.luowei.im.server.netty.handler;

import io.luowei.im.common.cache.distribute.DistributedCacheService;
import io.luowei.im.common.domain.constants.IMConstants;
import io.luowei.im.common.domain.enums.IMCmdType;
import io.luowei.im.common.domain.model.IMSendInfo;
import io.luowei.im.server.holder.SpringContextHolder;
import io.luowei.im.server.netty.cache.UserChannelContextCache;
import io.luowei.im.server.netty.processor.MessageProcessor;
import io.luowei.im.server.netty.processor.factory.ProcessorFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMChannelHandler extends SimpleChannelInboundHandler<IMSendInfo> {
    private final Logger logger = LoggerFactory.getLogger(IMChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMSendInfo imSendInfo) throws Exception {
        MessageProcessor processor = ProcessorFactory.getProcessor(IMCmdType.fromCode(imSendInfo.getCmd()));
        processor.process(ctx, processor.transForm(imSendInfo.getData()));
    }

    // 异常捕获方法
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("IMChannelHandler.exceptionCaught|异常:{}", cause.getMessage());
    }

    // 连接成功建立后的回调
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("IMChannelHandler.handlerAdded|{}连接", ctx.channel().id().asLongText());
    }

    // 连接断开时的回调
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        AttributeKey<Long> userAttrId = AttributeKey.valueOf(IMConstants.USER_ID);
        Long userId = ctx.channel().attr(userAttrId).get();

        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
        Integer terminal = ctx.channel().attr(terminalAttr).get();

        ChannelHandlerContext channelCtx = UserChannelContextCache.getChannelCtx(userId, terminal);

        // 防止异地登录误删
        if (channelCtx != null && channelCtx.channel().id().equals(ctx.channel().id())) {
            // 删除对应的channel
            UserChannelContextCache.removeChannelCtx(userId,terminal);
            DistributedCacheService distributedCacheService = SpringContextHolder.getBean(IMConstants.DISTRIBUTED_CACHE_REDIS_SERVICE_KEY);
            String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, userId.toString(), terminal.toString());
            distributedCacheService.delete(redisKey);
            logger.info("IMChannelHandler.handlerRemoved|断开连接, userId:{}, 终端类型:{}", userId, terminal);

        }
    }

    // 连接超时的回调
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE){
                AttributeKey<Long> attr = AttributeKey.valueOf(IMConstants.USER_ID);
                Long userId = ctx.channel().attr(attr).get();

                AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
                Integer terminal = ctx.channel().attr(terminalAttr).get();
                logger.info("IMChannelHandler.userEventTriggered|心跳超时.即将断开连接, userId:{}, 终端类型:{}", userId, terminal);
                ctx.channel().close();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
