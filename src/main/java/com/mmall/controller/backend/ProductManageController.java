package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seina on 2018/4/23.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 保存或者更新商品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        //校验当前是否处于已登录状态
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充service层增加或保存商品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessagr("无权限操作，需要管理员权限");
        }
    }

    /**
     * 产品上下架接口(实际上就是修改产品status)
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse updateProductStatus(HttpSession session, Integer productId, Integer status){
        //校验当前是否处于已登录状态
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充service层修改产品状态业务逻辑
            return iProductService.updateProductStatus(productId, status);
        }else{
            return ServerResponse.createByErrorMessagr("无权限操作，需要管理员权限");
        }
    }


    /**
     * 获取商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(HttpSession session, Integer productId){
        //校验当前是否处于已登录状态
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //在service层填充获取商品详情逻辑
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessagr("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取产品列表
     * @param session
     * @param pageNum 页码，第几页
     * @param pageSize 页大小
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        //校验当前是否处于已登录状态
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //在service层填充获取产品列表
            return iProductService.getProductList(pageNum, pageSize);
        }else{
            return ServerResponse.createByErrorMessagr("无权限操作，需要管理员权限");
        }
    }

    /**
     * 搜索产品
     * @param session
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchProduct(HttpSession session, Integer productId, String productName, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        //校验当前是否处于已登录状态
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充service层搜索产品的业务逻辑
            return iProductService.searchProduct(productId, productName, pageNum, pageSize);
        }else{
            return ServerResponse.createByErrorMessagr("无权限操作，需要管理员权限");
        }
    }

    /**
     * 上传图片（编辑产品时，上传产品图片）
     * @param session
     * @param file required=false 表示不传参数值会报错
     * @param request
     * @return 返回图片uri和图片地址url
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upLoad(HttpSession session, @RequestParam(value = "upload_file", required = false)MultipartFile file, HttpServletRequest request){
        //校验当前是否处于已登录状态
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充service层
            //根据servlet的上下文动态的创建一个相对路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessagr("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "richText_img_upload.do")
    @ResponseBody
    public Map richTextImgUpLoad(HttpSession session,  @RequestParam(value = "upload_file", required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        //校验当前是否处于已登录状态
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充service层搜索产品的业务逻辑
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }






















}
