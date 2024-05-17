package io.luowei.im.server.netty.tcp;

import io.luowei.im.server.netty.IMNettyServer;
import io.luowei.im.server.netty.handler.IMChannelHandler;
import io.luowei.im.server.netty.tcp.codec.TcpSocketMessageProtocolDecoder;
import io.luowei.im.server.netty.tcp.codec.TcpSocketMessageProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "tcp",value = "enable",havingValue = "true",matchIfMissing = true)
public class TcpSocketServer implements IMNettyServer {

    private final Logger logger = LoggerFactory.getLogger(TcpSocketServer.class);

    private volatile boolean ready = false;

    @Value("${tcp.port}")
    private int port;

    private EventLoopGroup workGroup;

    private EventLoopGroup bossGroup;

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();

        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(120, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast("encode",new TcpSocketMessageProtocolEncoder());
                        pipeline.addLast("decode",new TcpSocketMessageProtocolDecoder());
                        pipeline.addLast("handler",new IMChannelHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG,5)
                .childOption(ChannelOption.SO_KEEPALIVE,true);
        try {
            bootstrap.bind(port).sync().channel();
            this.ready = true;
            logger.info("TcpSocketServer.start|服务启动正常,监听端口:{}", port);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("TcpSocketServer.start|服务启动异常:{}", e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        if (bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown()){
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null && !workGroup.isShuttingDown() && !workGroup.isShutdown()){
            workGroup.shutdownGracefully();
        }
        this.ready = false;
        logger.info("TcpSocketServer.shutdown|服务停止");
    }
}
