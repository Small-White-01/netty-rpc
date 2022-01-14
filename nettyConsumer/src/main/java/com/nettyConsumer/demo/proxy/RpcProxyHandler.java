package com.nettyConsumer.demo.proxy;

import com.nettyConsumer.demo.client.NettyClient;
import com.rpccommon.demo.annotation.CustomMapping;
import com.rpccommon.demo.annotation.RpcProxyClient;
import com.rpccommon.demo.entity.Request;
import com.rpccommon.demo.entity.Response;
import com.rpccommon.demo.util.JSONUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RpcProxyHandler implements InvocationHandler , BeanPostProcessor{

    static Map<Method,Request> methodMap=new HashMap<>();


    @Autowired
    private NettyClient nettyClient;



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = methodMap.get(method);

        return (Response<?>) nettyClient.send(request);
    }


//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//
//        if(!bean.getClass().isAnnotationPresent(RpcProxyClient.class)){
//            return bean;
//        }
//
//        return Proxy.newProxyInstance(bean.getClass().getClassLoader(), new Class[]{bean.getClass()},
//                this);
//    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!bean.getClass().isAnnotationPresent(RpcProxyClient.class)) {
            return bean;
        }
        RpcProxyClient rpcProxyClient = bean.getClass().getAnnotation(RpcProxyClient.class);
        String servicePath = rpcProxyClient.value();
        if (servicePath.equals("")) {
            servicePath = "/";
        }
        Method[] declaredMethods = bean.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!method.isAnnotationPresent(CustomMapping.class)) {
                continue;
            }

            CustomMapping customMapping = method.getDeclaredAnnotation(CustomMapping.class);
            String path = customMapping.value();
            if (path.equals("")) {
                path = method.getName();
            }
            String fullPath = servicePath + path;
            if (fullPath.contains("//")) {
                fullPath = fullPath.replace("//", "/");
            }
            Request request=new Request();
            //    request.setMethodName(method.getName());
            //相当于http请求，这里封装了request
            request.setUrl(fullPath);
            request.setParameters(method.getParameters());
            request.setId(UUID.randomUUID().toString());
            request.setParameterTypes(method.getParameterTypes());
            Class<?> returnType = method.getReturnType();
            boolean isGenericReturnType=true;//返回值是否有泛型
            try {
                ParameterizedTypeImpl genericReturnType =
                        (ParameterizedTypeImpl)method.getGenericReturnType();
                Type[] actualTypeArguments = genericReturnType.getActualTypeArguments();
                request.setReturnGenericTypes(actualTypeArguments);
            }catch (ClassCastException classCastException){
                isGenericReturnType=false;
            }
            //将泛型参数与泛型类分开
            request.setReturnType(returnType);

            methodMap.put(method, request);

        }


        return Proxy.newProxyInstance(bean.getClass().getClassLoader(),
                new Class[]{bean.getClass()},this);


    }

}

