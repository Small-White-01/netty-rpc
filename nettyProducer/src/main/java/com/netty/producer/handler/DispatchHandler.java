package com.netty.producer.handler;

import com.rpccommon.demo.annotation.RpcService;
import com.rpccommon.demo.entity.Request;
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
public class DispatchHandler extends SimpleChannelInboundHandler<String>
    implements ApplicationContextAware {

//    @Autowired
//    private MessageHandlerContainer messageHandlerContainer;
//
//    @Autowired
//    private ThreadPoolExecutor threadPoolExecutor;





    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String request) throws Exception {
        Request request1 = JSONUtil.DeSerializeToObj(request.toString(), Request.class);
        String fullPath = request1.getUrl();
        String p= fullPath.substring(1);
        int i = p.indexOf("/");
        String servicePath = fullPath.substring(0, i);
        Object handler = this.handler(request1);
        channelHandlerContext.writeAndFlush(handler);
//        MessageHandler messageHandler = messageHandlerContainer.getMessageHandler(servicePath);
//        threadPoolExecutor.submit(()->{messageHandler.execute(channelHandlerContext,request1);});
    }
    static Map<String, Object> map=new HashMap();
    private Object handler(Request request) throws Exception {
        String className = request.getClassName();
        Object o = map.get(className);
        if(o!=null){
            Class<?> aClass = o.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            Method declaredMethod = aClass.getDeclaredMethod(methodName, parameterTypes);
            declaredMethod.setAccessible(true);
            return declaredMethod.invoke(o,parameters);

        }else {
            throw new Exception("no such service");
        }



    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        Collection<Object> values = beansWithAnnotation.values();
        for (Object clazz:values){
            Class<?>[] interfaces = clazz.getClass().getInterfaces();
            for (Class<?> i:interfaces){
                String simpleName = i.getName();
                map.put(simpleName,clazz);
            }

        }
    }
}
