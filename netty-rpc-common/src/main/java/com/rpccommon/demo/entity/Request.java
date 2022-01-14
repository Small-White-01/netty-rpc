package com.rpccommon.demo.entity;

import lombok.Data;

import java.lang.reflect.Type;

@Data
public class Request {

    private String id;
    private String url;
    private String className;
    private String methodName;
    private String info;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private RequestMethod requestMethod;
    private Class<?> returnType;
    private Type[] returnGenericTypes;



}
