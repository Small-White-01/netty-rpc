package com.nettyConsumer.demo.discovery;

import com.nettyConsumer.demo.client.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NettyChannelManager {


    private static final AtomicInteger rounding=new AtomicInteger(0);
    @Autowired
    private NettyClient nettyClient;

    private static CopyOnWriteArrayList<Channel> copyOnWriteArrayList=new CopyOnWriteArrayList<>();

    private static ConcurrentHashMap<SocketAddress,Channel>
    addressChannel=new ConcurrentHashMap<>();

    private List<Channel> channels=new ArrayList<>();

    public Channel chooseChannel(){
        if(copyOnWriteArrayList.size()==0)return null;
        int size = copyOnWriteArrayList.size();
        int index=( rounding.getAndIncrement()+size)%size;
        return copyOnWriteArrayList.get(index);
    }





    public void updateServerList(List<String> addressList){
        if(addressList==null||addressList.size()==0){
            System.out.println("节点为空");
            for(Channel channel:channels){
                SocketAddress socketAddress = channel.localAddress();
                addressChannel.get(socketAddress).close();

            }
            channels.clear();
            addressChannel.clear();;
            return;
        }

        HashSet<SocketAddress> addressHashSet=new HashSet<>();
        for(String url:addressList){
            String[] split = url.split(":");
            if(split.length==2){
                String s = split[0];
                String s1 = split[1];
                addressHashSet.add(new InetSocketAddress(s,Integer.parseInt(s1)));
            }else {
                throw new RuntimeException("url format error");
            }
        }



        for(SocketAddress socketAddress:addressHashSet){
            Channel channel = addressChannel.get(socketAddress);
            if(channel==null||!channel.isOpen()){
                processConnect(socketAddress);
            }else {
                System.out.println("通道处于连接状态");
            }


        }









    }

    private void processConnect(SocketAddress socketAddress) {
        try {
            Channel channel1 = nettyClient.doConnect(socketAddress);
            addToChannels(channel1,socketAddress);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void addToChannels(Channel channel,SocketAddress socketAddress) {
        channels.add(channel);
        addressChannel.put(socketAddress,channel);
    }


    public void removeChannel(Channel channel) {
        if(channels.contains(channel)){
            channel.close();
            channels.remove(channel);
        }
    }
}
