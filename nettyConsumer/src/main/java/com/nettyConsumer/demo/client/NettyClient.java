package com.nettyConsumer.demo.client;

import com.nettyConsumer.demo.discovery.NettyChannelManager;
import com.rpccommon.demo.codec.JsonDecoder;
import com.rpccommon.demo.codec.JsonEncoder;
import com.rpccommon.demo.entity.Request;
import com.rpccommon.demo.entity.Response;
import com.rpccommon.demo.util.JSONUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

@Component
public class NettyClient implements InitializingBean {

    private NioEventLoopGroup eventLoop=new NioEventLoopGroup();
    @Autowired
    private NettyClientHandler nettyClientHandler;
    @Autowired
    private NettyChannelManager nettyChannelManager;

    Bootstrap bootstrap=new Bootstrap();
    public void start(){
        bootstrap.group(eventLoop)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(30,
                                0,0))
                                .addLast("encoder", new JsonEncoder())
                                .addLast("decoder",new JsonDecoder())
                                .addLast(nettyClientHandler);
                    }
                });

    }

    public Response send(Request request){
        Channel channel = nettyChannelManager.chooseChannel();
        if(channel!=null&&channel.isActive()){
            SynchronousQueue<Object> queue = nettyClientHandler.sendRequest(request,channel);
            Response result=null;
            try {
                Object re = queue.take();
                result= JSONUtil.DeSerializeToObj(re.toString(), Response.class);
                return result;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }else {
            Response response= Response.builder().build();
            response.setCode(0);
            response.setRequestId(request.getId());
            response.setInfo("\"未正确连接到服务器.请检查相关配置信息!\"");
            return response;


        }
    }
    public Channel doConnect(SocketAddress address) throws InterruptedException {
        start();
        ChannelFuture future = bootstrap.connect(address);
        Channel channel = future.sync().channel();
        return channel;
    }






    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
