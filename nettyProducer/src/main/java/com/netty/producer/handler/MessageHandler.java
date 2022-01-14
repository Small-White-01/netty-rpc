package com.netty.producer.handler;

import com.rpccommon.demo.entity.Request;
import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler{

    void execute(ChannelHandlerContext ctx, Request request);




}
