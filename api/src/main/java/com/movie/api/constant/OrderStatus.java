package com.movie.api.constant;

/**
 * 订单状态
 */
public final class OrderStatus {

    /** 购物车锁座时长：10 分钟 */
    public final static long CART_LOCK_TIME = 1000 * 60 * 2;

    /** 订单支付超时：5 分钟（从创建订单起算） */
    public final static long PAYMENT_EXPIRATION_TIME = 1000 * 60 * 5;

    //初始化订单 等待支付
    public final static Integer PAYMENT_WAITING = 0;

    //支付失败
    public final static Integer PAYMENT_FAILED = 1;

    //支付成功
    public final static Integer PAYMENT_SUCCESSFUL = 2;

    //被管理员撤销
    public final static Integer COUNTERMAND = 3;

    //已退款
    public final static Integer REFUNDED = 4;

}
