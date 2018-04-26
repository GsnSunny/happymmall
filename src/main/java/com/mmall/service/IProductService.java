package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by ClanceRen on 2018/4/23.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse updateProductStatus(Integer productId, Integer status);
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);
    ServerResponse<PageInfo> searchProduct(Integer productId, String productName, Integer pageNum, Integer pageSize);
    ServerResponse<PageInfo> getPortalListBykeywordCategoryId(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
