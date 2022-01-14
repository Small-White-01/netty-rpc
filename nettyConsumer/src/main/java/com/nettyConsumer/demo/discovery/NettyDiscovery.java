package com.nettyConsumer.demo.discovery;

import com.rpccommon.demo.util.JSONUtil;
import com.zookeeper.demo.util.CuratorClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NettyDiscovery implements InitializingBean {



    private CuratorFramework client;


    private String connectString="127.0.0.1:2181";


    private static List<String> addressList=new ArrayList<>();
    private static final String nodeParent="zk/node/";

    @Override
    public void afterPropertiesSet() throws Exception {
        client= CuratorClientFactory.simpleClient(connectString);
        client.start();
        startListening();
    }


    public void startListening(){

        PathChildrenCache pathChildrenCache=new PathChildrenCache(client,nodeParent,true);
        PathChildrenCacheListener pathChildrenCacheListener=new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                switch (type){
                    case CHILD_ADDED:{
                        updateNodeList();
                    }case CHILD_REMOVED:{
                        ChildData data = pathChildrenCacheEvent.getData();
                        System.out.println("临时节点信息去除："+data.getPath()+new String(data.getData()));
                    }
                }
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateNodeList() {
        addressList.clear();
        try {
            List<String> list = client.getChildren()
                    .forPath(nodeParent);
            for (String path:list) {
                byte[] bytes = client.getData()
                        .forPath(nodeParent+path);
                String address = JSONUtil.DeSerializeToObj(new String(bytes), String.class);
                addressList.add(address);

            }
            updateAddressList(addressList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Autowired
    private NettyChannelManager nettyChannelManager;
    public void updateAddressList(List<String> addressList){
        nettyChannelManager.updateServerList(addressList);
    }


}
