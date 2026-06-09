package com.movie.api.utils;

import com.movie.api.constant.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public final class SeatUtil {

    private SeatUtil() {
    }

    public static List<Integer> parseSeatNumbers(String seats) {
        List<Integer> result = new ArrayList<>();
        if (seats == null || seats.isEmpty()) {
            return result;
        }
        for (String part : seats.split("号")) {
            if (!part.isEmpty()) {
                result.add(Integer.parseInt(part));
            }
        }
        return result;
    }

    /** 购物车锁座是否仍有效（10 分钟） */
    public static boolean isCartLockActive(String createAt) {
        return isLockActive(createAt, OrderStatus.CART_LOCK_TIME);
    }

    /** 待支付订单锁座是否仍有效（5 分钟） */
    public static boolean isOrderLockActive(String createAt) {
        return isLockActive(createAt, OrderStatus.PAYMENT_EXPIRATION_TIME);
    }

    private static boolean isLockActive(String createAt, long durationMs) {
        if (createAt == null || createAt.isEmpty()) {
            return false;
        }
        return DataTimeUtil.parseTimeStamp(createAt) + durationMs > System.currentTimeMillis();
    }
}
