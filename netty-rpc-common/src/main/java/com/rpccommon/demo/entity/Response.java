package com.rpccommon.demo.entity;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class Response {

    private String requestId;
    private Object data;
    private int code;
    private String info;
    private Exception exception;

    public void addInfo(String message){
        if(info==null){
            info="";
        }
        info= info + "\r\n" +
                message;
    }





}
