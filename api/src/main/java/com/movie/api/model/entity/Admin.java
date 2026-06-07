package com.movie.api.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员
 */
@Data
@TableName("t_admin")
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {

    private String id;

    // 用户名
    private String username;

    //昵称
    private String nickname;

    //手机号
    private String phone;

    //生日
    private String birthday;

    //性别
    private String gender;

    //个人简介
    private String info;

    private String password;

    private String avatar;

}
