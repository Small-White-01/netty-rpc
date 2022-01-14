package com.netty.producer.handler;

import com.rpccommon.demo.annotation.CustomMapping;
import com.rpccommon.demo.entity.RequestMethod;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class MessageHandlerContainer implements InitializingBean {
    /**
     * 路径到方法
     */
    Map<String,MessageHandler> requestMethodMap=new HashMap<>();


    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, MessageHandler> beans = applicationContext.getBeansOfType(MessageHandler.class);
        beans.values()
                .forEach(messageHandler -> {
                    String path=null;
                    if(messageHandler.getClass().isAnnotationPresent(CustomMapping.class)){
                        CustomMapping declaredAnnotation = messageHandler
                                .getClass().getDeclaredAnnotation(CustomMapping.class);
                        path = declaredAnnotation.value();
                        if("".equals(path)){
                            path=messageHandler.getClass().getSimpleName().toLowerCase(Locale.ROOT);
                        }
                    }

                    requestMethodMap.put(path, messageHandler);
                });


    }

    public MessageHandler getMessageHandler(String type){

        try{
            if(!requestMethodMap.containsKey(type)){
                throw new RuntimeException("handler not found,please define the url");
            }
            return requestMethodMap.get(type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;


    }

}
