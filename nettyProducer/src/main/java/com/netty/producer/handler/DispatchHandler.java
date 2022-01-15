package com.netty.producer.handler;

import com.rpccommon.demo.annotation.RpcService;
import com.rpccommon.demo.entity.Request;
import com.rpccommon.demo.entity.RequestMethod;
import com.rpccommon.demo.entity.Response;
import com.rpccommon.demo.util.JSONUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@ChannelHandler.Sharable
@Component
public class DispatchHandler extends SimpleChannelInboundHandler<String> {

    @Autowired
    private MessageHandlerContainer messageHandlerContainer;
//
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private MessageServiceContainer messageServiceContainer;



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String request) throws Exception {
        Request request1 = JSONUtil.DeSerializeToObj(request.toString(), Request.class);
        RequestMethod requestMethod = request1.getRequestMethod();
        if(requestMethod.equals(RequestMethod.RPC)) {
            threadPoolExecutor.submit(() -> {
                Response response = null;
                response = this.handler(request1);
                channelHandlerContext.writeAndFlush(response);
            });
        }else if(requestMethod.equals(RequestMethod.HEARTBEAT)){
            String heartBeat = request1.getUrl();
            MessageHandler messageHandler = messageHandlerContainer.getMessageHandler(heartBeat);
            threadPoolExecutor.submit(()->{messageHandler.execute(channelHandlerContext,request1);});
        }



    }
    private Response handler(Request request)  {
        Response.ResponseBuilder responseBuilder=Response.builder();
        String fullPath = request.getUrl();
        String p= fullPath.substring(1);
        int i = p.indexOf("/");
        String servicePath = fullPath.substring(0, i);
        Object service = messageServiceContainer.getService(servicePath);
        if(service==null){
            responseBuilder.info("service is null")
                    .code(0);
            return responseBuilder.build();
        }
        String methodName = fullPath.substring(i + 1);
        Method declaredMethod=null;
        try {
             declaredMethod= service.getClass()
                    .getDeclaredMethod(methodName, request.getParameterTypes());
        }catch (NoSuchMethodException |SecurityException e){
            responseBuilder.info(e.getMessage())
                    .code(0)
                    .data(null);
            return responseBuilder.build();
        }
        Object[] parameters1 = request.getParameters();
        if(declaredMethod.getParameterTypes().length!=parameters1.length){
            responseBuilder.info("paremeters not matches!")
                    .code(0)
                    .data(null);
            return responseBuilder.build();
        }
        Object o= null;
        try {
            o = declaredMethod.invoke(service, parameters1);
        } catch (IllegalAccessException | InvocationTargetException e) {
            responseBuilder.exception(e).code(0).info("error");
            return responseBuilder.build();
        }
        if(o!=null){
            responseBuilder.data(o)
                    .info("success")
                    .code(1);
        }else {
            responseBuilder.code(0)
                    .info("failed")
                    .data(null);
        }
        return responseBuilder.build();



    }



}
