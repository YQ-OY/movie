package com.movie.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusVO {

    /** 已支付成功，不可选（红色） */
    private List<Integer> soldSeats = new ArrayList<>();

    /** 购物车/待支付订单 15 分钟内锁座，不可选（蓝色） */
    private List<Integer> lockedSeats = new ArrayList<>();
}
