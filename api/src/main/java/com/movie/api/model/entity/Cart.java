package com.movie.api.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 购物车
 */
@Data
@TableName("t_cart")
@NoArgsConstructor
public class Cart implements Serializable {

    private String id;

    //用户id
    private String uid;

    //场次id（与 t_arrangement.id 一致，自增）
    private Long aid;

    //座位号
    private String seats;

    private String phone;

    private Integer status;

    //金额
    private double price;

    //加入购物车时间（10 分钟锁座）
    private String createAt;

}
