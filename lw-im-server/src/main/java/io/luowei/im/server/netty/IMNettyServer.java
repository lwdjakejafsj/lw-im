package io.luowei.im.server.netty;

public interface IMNettyServer {

    // 服务是否准备就绪
    boolean isReady();

    // 启动服务
    void start();

    // 停止服务
    void shutdown();

}
