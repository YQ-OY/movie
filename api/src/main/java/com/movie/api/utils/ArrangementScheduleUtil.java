package com.movie.api.utils;

import com.movie.api.model.entity.Arrangement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 排片放映区间：左闭右开 [start, endExclusive)，便于判断相邻场次不冲突。
 */
public final class ArrangementScheduleUtil {

    private static final DateTimeFormatter HMS = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ArrangementScheduleUtil() {
    }

    public static LocalDateTime startDateTime(String date, String startTime) {
        LocalDate d = LocalDate.parse(date.trim());
        LocalTime t = LocalTime.parse(startTime.trim());
        return LocalDateTime.of(d, t);
    }

    /**
     * 根据库中 date + startTime + endTime 得到场次结束时刻（右开区间的右端）。
     * 若结束钟点不晚于开始（跨午夜），则结束日记为放映日期的次日。
     */
    public static LocalDateTime exclusiveEndFromStored(String date, String startTime, String endTime) {
        LocalDateTime start = startDateTime(date, startTime);
        LocalDate d = LocalDate.parse(date.trim());
        LocalTime et = LocalTime.parse(endTime.trim());
        LocalDateTime end = LocalDateTime.of(d, et);
        if (!end.isAfter(start)) {
            end = end.plusDays(1);
        }
        return end;
    }

    public static LocalDateTime exclusiveEndFromStartAndDuration(LocalDateTime start, int durationMinutes) {
        return start.plusMinutes(durationMinutes);
    }

    public static String formatHms(LocalDateTime t) {
        return HMS.format(t.toLocalTime());
    }

    public static LocalDateTime exclusiveEndForArrangement(Arrangement a) {
        return exclusiveEndFromStored(a.getDate(), a.getStartTime(), a.getEndTime());
    }

    /**
     * 左闭右开区间是否相交。
     */
    public static boolean intervalsOverlap(LocalDateTime s1, LocalDateTime e1Ex, LocalDateTime s2, LocalDateTime e2Ex) {
        return s1.isBefore(e2Ex) && s2.isBefore(e1Ex);
    }

    /** 开场前停止售票的提前分钟数（含边界：到达该时刻起不可售） */
    public static final int TICKET_SALES_CLOSE_BEFORE_MINUTES = 20;

    /**
     * 是否仍允许购票：当前时间须严格早于「开场时间 − TICKET_SALES_CLOSE_BEFORE_MINUTES 分钟」。
     */
    public static boolean isTicketSaleAllowed(Arrangement a) {
        if (a == null || a.getDate() == null || a.getStartTime() == null) {
            return false;
        }
        try {
            LocalDateTime start = startDateTime(a.getDate(), a.getStartTime());
            LocalDateTime stopSalesAt = start.minusMinutes(TICKET_SALES_CLOSE_BEFORE_MINUTES);
            return LocalDateTime.now().isBefore(stopSalesAt);
        } catch (Exception e) {
            return false;
        }
    }
}
