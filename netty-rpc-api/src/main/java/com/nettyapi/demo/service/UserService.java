package com.nettyapi.demo.service;

import com.rpccommon.demo.annotation.CustomMapping;
import com.rpccommon.demo.annotation.RpcProxyClient;
import com.rpccommon.demo.entity.Request;
import com.rpccommon.demo.entity.RequestMethod;
import com.rpccommon.demo.entity.Response;

@RpcProxyClient("/user")
public interface UserService {

    @CustomMapping("/getData")
    Response getData(String id);
    @CustomMapping(value = "/insertData",requestMethod = RequestMethod.POST)
    void insertData(Request data);
    @CustomMapping(value = "/sendHeart",requestMethod = RequestMethod.HEARTBEAT)
    void sendHeartBeat();

//    void deleteData(String id);

//    Response<T> rpc(String url, Class<T> tClass);


}
