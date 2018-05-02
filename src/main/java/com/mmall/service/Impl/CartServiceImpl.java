package com.mmall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ClanceRen on 2018/4/27.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    //新增一个产品到购物车
    public ServerResponse<CartVo> addCart(Integer userId, Integer productId, Integer count){
        //首先对参数进行校验
        if (productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //查看购物车里是否有该商品
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null){ //产品不再购物车里，需要新增一个产品
            Cart cartNew = new Cart();
            cartNew.setId(productId);
            cartNew.setQuantity(count);
            cartNew.setChecked(Const.Cart.CHECKED);
            cartNew.setUserId(userId);
            cartMapper.insert(cartNew);
        }else{
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKey(cart);
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> list (Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count){
        if (productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null){
            cart.setQuantity(count);
        }
        int rowCount = cartMapper.updateByPrimaryKey(cart);
        if (rowCount <= 0){
            return ServerResponse.createByErrorMessagr("购物车更新失败");
        }
        return list(userId);
    }


    public ServerResponse<CartVo> delete(Integer userId, String productIds){
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productIdList);
        return list(userId);
    }

    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId ){
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    //核心类，传入用户id，将cart封装成CartVo
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //根据用户id查出该用户的购物车
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        //购物车中所有商品总价
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()){//商品库存，大于购物车中用户所购买的商品数量
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        //private String limitQuantity;//限制数量的一个返回结果
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        //库存不充足的时候，将购物车中该商品的数量更新设置为该商品的库存数量
                        buyLimitCount = product.getStock();
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    //private Integer quantity;//购物车中此商品的数量
                    cartProductVo.setQuantity(buyLimitCount);
                    //private BigDecimal productTotalPrice;//单个商品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity().doubleValue()));
                }
                //private Integer productChecked;//此商品是否勾选
                cartProductVo.setProductChecked(cartItem.getChecked());
                if (cartItem.getChecked() == Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.isAllChecked(userId));
        return cartVo;
    }


    //判断购物车中商品是否都被勾选
    private boolean isAllChecked(Integer userId){
        if (userId != null){
            return false;
        }
        //等于0表示未选中的商品数为0，即全被选中
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }












}
