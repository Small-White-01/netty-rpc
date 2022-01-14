package com.nettyConsumer.demo.controller;

import com.nettyapi.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("getdata")
    public String getData(String id){
        return userService.getData(id).toString();
    }

    @Scheduled
    public void sendHeartBeat(){
        userService.sendHeartBeat();
    }


}
