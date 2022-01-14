package com.netty.producer.register;

import com.netty.producer.NettyChannelManager;
import com.zookeeper.demo.util.CuratorClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ServerRegister implements InitializingBean {


    @Value("${zookeeper.address}")
    private String connectString;

    private CuratorFramework client;

    private static final String nodePrefix="/zk/node/produce-";



    @Override
    public void afterPropertiesSet() throws Exception {
        client=CuratorClientFactory.createCustomClient(connectString
        ,60*1000*60,
                60*1000*60);
    }

    public void register(String address){
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(
                            CreateMode.EPHEMERAL_SEQUENTIAL
                    )
                    .forPath(nodePrefix,address.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }





}
