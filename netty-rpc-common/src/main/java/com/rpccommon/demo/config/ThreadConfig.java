package com.rpccommon.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadConfig {


    private static final int coreSize=Runtime.getRuntime().availableProcessors();


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(2*coreSize,2*coreSize
        ,60000
        , TimeUnit.MILLISECONDS
        ,new LinkedBlockingQueue<>());

    }


}
