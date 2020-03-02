package com.zjy.cost;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by com.com.zjy on 2019-05-03
 * TODO: 这里TimeTotal注解要和asmAnnotation源码中的TimeTotal注解一样
 * TODO: 包名+类名：都要一样
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface TimeTotal {
}
