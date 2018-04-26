package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by ClanceRen on 2018/4/11.
 */
public class Const {
    //将校验username和email的type声明两个常量
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String CURRENT_USER = "currentUser";

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public enum ProductStatusEnum{
        ON_SALE("在线",1);
        private String value;
        private int code;
        ProductStatusEnum(String value, int code){
            this.code = code;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public int getCode(){
            return code;
        }
    }
}
