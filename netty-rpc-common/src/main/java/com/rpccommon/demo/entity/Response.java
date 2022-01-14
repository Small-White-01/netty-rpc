package com.rpccommon.demo.entity;

import lombok.Data;

@Data
public class Response<T> {

    private String requestId;
    private T data;
    private int code;
    private String info;



}
