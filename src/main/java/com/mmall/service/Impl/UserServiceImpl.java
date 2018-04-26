package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by ClanceRen on 2018/4/10.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServerResponse<User> Login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){//等于0表示用户名不存在
            //调用泛型做的高可复用服务端响应类
            return ServerResponse.createByErrorMessagr("用户名不存在");
        }

        //todo 密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessagr("密码错误");
        }

        //把密码设置为空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }
    /*
    用户注册
     */
    public ServerResponse<String> register(User user){
        //复用实时校验方法
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()){//如果成功，返回校验成功，前面加非！，则返回用户名已存在或者参数错误
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //设置用户为普通会员
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);//resultCount表示插入数据行数
        if(resultCount == 0){
            return ServerResponse.createByErrorMessagr("注册失败");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    /*
    忘记密码中的重置密码功能
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPass, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessagr("参数错误，Token需要传递");
        }
        //验证用户名是否存在
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){//等于0表示用户名不存在
            //调用泛型做的高可复用服务端响应类
            return ServerResponse.createByErrorMessagr("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){//如果从缓存中取出的token为空
            return ServerResponse.createByErrorMessagr("token无效或者过期");
        }
        if(StringUtils.equals(token, forgetToken)){
            String md5Pass = MD5Util.MD5EncodeUtf8(newPass);
            int rowCount = userMapper.updateByUsername(username, md5Pass);
            if(rowCount > 0){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessagr("token错误，请重新获取重置密码里的token");
        }
        return ServerResponse.createByErrorMessagr("修改密码失败");
    }

    /*
        检查答案是否正确
         */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount>0){//如果返回结果大于0说明这个问题和答案是这个用户的
            //获取一个唯一标识码作为forgetToken
            String forgetToken = UUID.randomUUID().toString();
            //将forgetToken放到本地缓存
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            //返回forgetToken给客户端，下次请求的时候携带
            return ServerResponse.createBySuccess(forgetToken);
        }
        //问题的答案错误
        return ServerResponse.createByErrorMessagr("问题的答案错误");
    }


    /* 实时校验email和username */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){//如果type不为空
            if(Const.USERNAME.equals(type)){//如果type是用户名，那么判断用户名是否已存在
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){//大于0表示该用户名已存在不可以注册
                    return ServerResponse.createByErrorMessagr("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessagr("email已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessagr("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }


    @Override
    public ServerResponse<String> selectQuestion(String username) {
        //首先校验用户名
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){//方法如果成功，说明用户名不存在
            return ServerResponse.createByErrorMessagr("用户名不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessagr("找回密码的问题是空的");
    }




    /* 登录状态下重置密码 */
    @Override
    public ServerResponse<String> resetPassword(User user, String oldPass, String newPass) {
        //防止横向越权，首先验证旧密码是否正确
        /**
         *  String md5Password = MD5Util.MD5EncodeUtf8(password);
         User user = userMapper.selectLogin(username,md5Password);
         if(user == null){
         return ServerResponse.createByErrorMessagr("密码错误");
         }
         */
        int resultCount = userMapper.checkOldPass(user.getId(), MD5Util.MD5EncodeUtf8(oldPass));
        if(resultCount == 0){
            return ServerResponse.createByErrorMessagr("旧密码错误");
        }

        //更改密码
        user.setPassword(MD5Util.MD5EncodeUtf8(newPass));
        int result = userMapper.updateByPrimaryKeySelective(user);
        if(result > 0){
            return ServerResponse.createBySuccess("密码更新成功");
        }
        return ServerResponse.createByErrorMessagr("密码更新错误");
    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {
        //service层主要用来处理返回用户的密码置空功能
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null) {
            return ServerResponse.createByErrorMessagr("找不到用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /* 登录状态更改用户信息 */
    @Override
    public ServerResponse<User> update_information(User user) {
        //username 不能被更新
        //校验新的email是否已经存在，并且不能是当前用户的email
        int resultCount = userMapper.checkEmailByCurrentId(user.getId(), user.getEmail());
        if(resultCount > 0) {//大于0说明新的邮箱已经存在
            return ServerResponse.createByErrorMessagr("email已经存在，请更换eamil");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int result = userMapper.updateByPrimaryKeySelective(updateUser);
        if(result > 0){
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessagr("更新个人信息失败");
    }

    //校验当前用户登录是否是管理员身份
    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
