package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.springframework.stereotype.Service;


/**
 * Created by ClanceRen on 2018/4/10.
 */
public interface IUserService {
    ServerResponse<User> Login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> selectQuestion(String username);
    ServerResponse<String> checkAnswer(String username, String question, String answer);
    ServerResponse<String> forgetResetPassword(String username, String newPass, String forgetToken);
    ServerResponse<String> resetPassword(User user, String oldPass, String newPass);
    ServerResponse<User> update_information(User user);
    ServerResponse<User> getInformation(Integer id);
    ServerResponse<String> checkAdminRole(User user);
}
