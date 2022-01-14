package com.netty.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.rpccommon.demo","com.netty.producer"})
public class NettyProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NettyProducerApplication.class);
    }
}
