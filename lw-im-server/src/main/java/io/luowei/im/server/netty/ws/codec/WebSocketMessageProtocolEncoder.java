package io.luowei.im.server.netty.ws.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.luowei.im.common.domain.model.IMSendInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public class WebSocketMessageProtocolEncoder  extends MessageToMessageEncoder<IMSendInfo> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMSendInfo imSendInfo, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String value = objectMapper.writeValueAsString(imSendInfo);
        TextWebSocketFrame frame = new TextWebSocketFrame(value);
        list.add(frame);
    }
}
