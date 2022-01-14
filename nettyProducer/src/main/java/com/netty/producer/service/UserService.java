package com.netty.producer.service;

public interface UserService {

    Object getUser(String id);

    void insert(String user);

}
