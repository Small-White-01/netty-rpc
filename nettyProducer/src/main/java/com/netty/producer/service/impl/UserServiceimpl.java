package com.netty.producer.service.impl;

import com.netty.producer.service.UserService;
import com.rpccommon.demo.annotation.RpcService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RpcService
public class UserServiceimpl implements UserService {
    static Map<String,String> map=new HashMap<>();
    @Override
    public Object getUser(String id) {
        return new String("user");
    }

    @Override
    public void insert(String user) {
        int size = map.size();
        map.put(size+"","user"+size);
    }
}
