package com.movie.api.utils;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电影上映日期解析与阶段判断（YYYY-MM-DD 与 YYYY年MM月DD日）。
 */
public final class FilmReleaseUtil {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Pattern CN_PATTERN = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日");

    private FilmReleaseUtil() {
    }

    /** 当天日期键 YYYY-MM-DD */
    public static String todayKey() {
        return LocalDate.now(ZONE).format(ISO);
    }

    /**
     * 将上映日期统一为 YYYY-MM-DD；无法解析时返回 null。
     */
    public static String normalizeReleaseTime(String releaseTime) {
        if (!StringUtils.hasText(releaseTime)) {
            return null;
        }
        String trimmed = releaseTime.trim();
        try {
            return LocalDate.parse(trimmed, ISO).format(ISO);
        } catch (DateTimeParseException ignored) {
            // fall through
        }
        Matcher m = CN_PATTERN.matcher(trimmed);
        if (m.matches()) {
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            return LocalDate.of(year, month, day).format(ISO);
        }
        return null;
    }

    /** 是否正在上映：releaseTime <= 今天，且可解析 */
    public static boolean isShowing(String releaseTime) {
        String key = normalizeReleaseTime(releaseTime);
        if (key == null) {
            return false;
        }
        return !key.isEmpty() && key.compareTo(todayKey()) <= 0;
    }

    /** 是否待上映：releaseTime > 今天，且可解析 */
    public static boolean isUpcoming(String releaseTime) {
        String key = normalizeReleaseTime(releaseTime);
        if (key == null) {
            return false;
        }
        return key.compareTo(todayKey()) > 0;
    }
}
