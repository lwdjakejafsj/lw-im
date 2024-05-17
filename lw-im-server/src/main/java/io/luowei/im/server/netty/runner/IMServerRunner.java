package io.luowei.im.server.netty.runner;

import cn.hutool.core.collection.CollectionUtil;
import io.luowei.im.server.netty.IMNettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * SpringBoot启动时启动所有netty服务
 * author: luowei
 * date:
 */
@Component
public class IMServerRunner implements CommandLineRunner {

    @Autowired
    private List<IMNettyServer> imNettyServerList;

    /**
     * 判断服务是否准备完毕
     */
    public boolean isReady(){
        for (IMNettyServer imNettyServer : imNettyServerList){
            if (!imNettyServer.isReady()){
                return false;
            }
        }
        return true;
    }

    @Override
    public void run(String... args) throws Exception {
        //启动每个服务
        if (!CollectionUtil.isEmpty(imNettyServerList)){
            imNettyServerList.forEach(IMNettyServer::start);
        }
    }

    @PreDestroy
    public void destroy(){
        if (!CollectionUtil.isEmpty(imNettyServerList)){
            imNettyServerList.forEach(IMNettyServer::shutdown);
        }
    }
}
