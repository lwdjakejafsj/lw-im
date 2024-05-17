package io.luowei.im.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"io.luowei.im"})
@SpringBootApplication
public class IMServerStarter {
    public static void main(String[] args) {
        SpringApplication.run(IMServerStarter.class,args);
    }
}
