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

    /** 今日场次 */
    private long todayArrangementTotal;

    /** 今日订单 */
    private long todayOrderTotal;

    /** 今日营收 */
    private double todayRevenueTotal;

    /** 今日上座率/转化率等综合指标 */
    private double todayConversionRate;

    private List<StatItemVO> orderStatusPie = new ArrayList<>();

    private List<StatItemVO> topFilmsByRevenue = new ArrayList<>();

    /** 近 7 天每日订单量 */
    private List<StatItemVO> ordersByDay = new ArrayList<>();

    /** 近 7 天每日营收 */
    private List<StatItemVO> revenueByDay = new ArrayList<>();

    /** 今日按小时分布的购票热力图数据 */
    private List<HeatmapItemVO> hourlyTicketHeatmap = new ArrayList<>();

    /** 影片类型营收占比 */
    private List<StatItemVO> revenueByFilmType = new ArrayList<>();

    /** 购票转化漏斗 */
    private List<StatItemVO> purchaseFunnel = new ArrayList<>();
}
