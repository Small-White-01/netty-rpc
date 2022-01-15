package com.rpccommon.demo.annotation;

import com.rpccommon.demo.entity.RequestMethod;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CustomMapping {


    String value() default "";

    RequestMethod requestMethod() default RequestMethod.GET;
}
