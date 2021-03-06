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

    public interface Cart{
        int CHECKED = 1;//被选中状态
        int UN_CHECKED = 0;

        //限制购物车中产品数量小于库存数量
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";//若大于库存，返回失败，不可以继续增加产品数量
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";//若小于库存，返回成功，则可以继续增加产品数量
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

    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        private int code;
        private String value;
        OrderStatusEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public interface AliPayCallback{

        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        private int code;
        private String value;

        PayPlatformEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }
}
