package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Seina on 2018/4/26.
 */

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 前台获取商品详情
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        return iProductService.manageProductDetail(productId);
    }



    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> List(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                         @RequestParam(value = "categoryId", required = false) String keyword,
                                         @RequestParam(value = "categoryId", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "categoryId", defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "categoryId", defaultValue = "") String orderBy){
        return iProductService.getPortalListBykeywordCategoryId(keyword, categoryId, pageNum ,pageSize, orderBy);
    }
















}
