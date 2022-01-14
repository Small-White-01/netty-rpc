package com.netty.producer.server;

import com.netty.producer.register.ServerRegister;
import com.rpccommon.demo.codec.JsonDecoder;
import com.rpccommon.demo.codec.JsonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyServer implements ApplicationContextAware, InitializingBean {


    @Value("${rpc.address}")
    private String nettyAddress;

    @Autowired
    private ServerRegister serverRegister;

    private static final EventLoopGroup boss=new NioEventLoopGroup(1);
    private static final EventLoopGroup worker=new NioEventLoopGroup(4);





    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }



    public void start(){

        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(60,
                                0,0))
                                .addLast("encoder",new JsonEncoder())
                                .addLast("decoder",new JsonDecoder())
                                .addLast(new NettyServerHandler());
                    }
                });

            String[] address = nettyAddress.split(":");

            String ip = address[0];
            String port = address[1];
        try {
            ChannelFuture future = serverBootstrap.bind(ip, Integer.parseInt(port)).sync();
            log.info("服务端启动，监听地址为:{},端口为：{}",ip,port);
            serverRegister.register(nettyAddress);
            future.channel().close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }



}
