package com.rpccommon.demo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtil {


    private static Gson gson;

    static {
        gson=new GsonBuilder().create();
    }

    public static String serializeToJSON(Object o){
        return gson.toJson(o);
    }


    public static <T> T DeSerializeToObj(String json,Class<T> tClass){
        return gson.fromJson(json,tClass);
    }



}
