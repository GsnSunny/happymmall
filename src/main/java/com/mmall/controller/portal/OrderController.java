package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.Impl.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ClanceRen on 2018/5/1.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);


    @Autowired
    private IOrderService iOrderService;

    /**
     * (支付模块一)请求预下单接口，返回二维码，供用户扫描支付
     * @param session
     * @param orderNo 订单号
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessagr("用户未登录");
        }
        //用HttpServletRequest拿到servlet上下文，拿到"upload"文件夹，
        //然后把自动生成的二维码，传到ftp服务器上，然后返回给前端一个二维码的图片地址，
        //然后前端把图片地址进行一个展示，然后扫码支付
        String path = request.getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(), orderNo, path);
    }


    /**
     * (支付模块二)支付宝两次回调函数
     * @param request 支付宝的请求参数都在request里
     * @return "success" or "failed"
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        /**
         * 1、从阿里回调request中取出参数
         */
        //声明一个参数
        Map<String, String> params = Maps.newHashMap();
        //取出request里阿里回调的参数，放在map里
        Map requestParams = request.getParameterMap();
        //通过迭代器取出参数
        //keySet()将map里的所有key取出放到Set集合里，Set可以使用迭代器，返回Set<k>
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values =  (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i=0; i<values.length; i++){
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i]+",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

        /**
         * 2、验证数字签名：验证回调的正确性，是不是支付宝发的
         */
        params.remove("sign_type");
        try {
            boolean checkResult = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!checkResult){
                return ServerResponse.createByErrorMessagr("非法请求，验证不通过，在恶意请求就找网警报警了！！");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常", e);
        }

        /**
         * 3、验证数据的正确性
         */
        if (iOrderService.aliPayCallback(params).isSuccess()){
            return Const.AliPayCallback.RESPONSE_SUCCESS;
        }
        return Const.AliPayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单支付状态
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (iOrderService.selectOrderPayStatus(user.getId(), orderNo).isSuccess()){
            return ServerResponse.createBySuccess();//true
        }
        return ServerResponse.createByError();//false
    }
}
