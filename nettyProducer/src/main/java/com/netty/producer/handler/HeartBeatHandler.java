package com.netty.producer.handler;

import com.rpccommon.demo.annotation.CustomMapping;
import com.rpccommon.demo.annotation.RpcProxyClient;
import com.rpccommon.demo.entity.Request;
import com.rpccommon.demo.entity.Response;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@CustomMapping("/heartbeat")
@Slf4j
public class HeartBeatHandler implements MessageHandler{
    @Override
    public void execute(ChannelHandlerContext ctx, Request request) {
        String info = request.getInfo();
        log.info("收到客户端心跳信息:{}",info);
        String id = request.getId();
        Response response=new Response();
        response.setCode(1);
        response.setRequestId(id);
        response.setInfo("已收到服务端心跳");
        ctx.writeAndFlush(response);
    }

    @Override
    public String type() {
        return "heartBeat";
    }
}
