package io.luowei.im.server.netty.tcp.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.luowei.im.common.domain.model.IMSendInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * Tcp消息编码
 * author: luowei
 * date:
 */
public class TcpSocketMessageProtocolEncoder extends MessageToByteEncoder<IMSendInfo> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMSendInfo imSendInfo, ByteBuf byteBuf) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(imSendInfo);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
