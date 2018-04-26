package com.mmall.common;

/**
 * Created by ClanceRen on 2018/4/10.
 */
public enum ResponseCode {//响应编码的枚举类

    //枚举变量
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    //自定义变量
    private final int code;
    private final String desc;//description

    ResponseCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    //提供两个方法把属性暴露出去
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }

















}