package com.movie.api.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端「运营概览」仪表盘数据
 */
@Data
public class DashboardStatsVO {

    private long orderTotal;

    private long orderPaid;

    private long orderPending;

    private long orderFailed;

    private long orderCancelled;

    private double revenueTotal;

    private long userTotal;

    private long filmTotal;

    private List<StatItemVO> orderStatusPie = new ArrayList<>();

    private List<StatItemVO> topFilmsByRevenue = new ArrayList<>();

    /** 近 14 天每日下单量（按 createAt 日期） */
    private List<StatItemVO> ordersByDay = new ArrayList<>();
}
