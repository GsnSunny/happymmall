package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by ClanceRen on 2018/5/1.
 */
public interface IOrderService {

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse aliPayCallback(Map<String, String> params);

    ServerResponse selectOrderPayStatus(Integer userId, Long orderNo);
}
