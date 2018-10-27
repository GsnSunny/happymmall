package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by Seina on 2018/4/27.
 */
public interface ICartService {

    ServerResponse<CartVo> addCart(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> list (Integer userId);
    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> delete(Integer userId, String productIds);
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);
    ServerResponse<Integer> getCartProductCount(Integer userId);

}
