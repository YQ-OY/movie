package com.movie.api.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单
 */
@Data
@TableName("t_order")
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private String id;

    //用户id
    private String uid;

    //取票电话
    private String phone;

    //场次id（与 t_arrangement.id 自增主键一致）
    private Long aid;

    //座位
    private String seats;

    //金额
    private double price;

    //状态
    private Integer status;  //0.待支付；1.支付失败；2.支付成功；3.订单已被管理员撤销

    //创建时间
    private String createAt;

    //更新时间
    private String payAt;

}
