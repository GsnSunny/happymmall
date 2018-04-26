package com.mmall.dao;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //登录是检验用户名是否存在
    int checkUsername(String username);

    //登录是检验邮箱是否存在
    int checkEmail(String email);

    //校验email是否已经存在，并且不是当前用户的
    int checkEmailByCurrentId(@Param("id")Integer id, @Param("email") String email);

    //判断用户名密码是否正确
    User selectLogin(@Param("username")String username, @Param("password")String password);

    String selectQuestion(String username);

    int checkAnswer(@Param("username") String username, @Param("question")String question, @Param("answer")String answer);

    int updateByUsername(@Param("username") String username, @Param("newPass") String newPass);

    int checkOldPass(@Param("userId")int userId, @Param("oldPass")String oudPass);

}