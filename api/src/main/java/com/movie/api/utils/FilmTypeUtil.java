package com.movie.api.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.api.model.entity.Film;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 电影类型多选：库内以英文逗号拼接存储，查询支持多类型模糊匹配。
 */
public final class FilmTypeUtil {

    private static final String SEPARATOR = ",";

    private FilmTypeUtil() {
    }

    /** 规范化为「类型1,类型2」；无有效类型时返回 null */
    public static String normalizeType(String type) {
        if (!StringUtils.hasText(type)) {
            return null;
        }
        Set<String> parts = new LinkedHashSet<>();
        for (String part : type.split("[,，、\\s]+")) {
            String trimmed = part.trim();
            if (StringUtils.hasText(trimmed)) {
                parts.add(trimmed);
            }
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(SEPARATOR, parts);
    }

    public static List<String> splitTypes(String type) {
        String normalized = normalizeType(type);
        if (normalized == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(normalized.split(SEPARATOR))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    /** 任一查询类型命中即匹配（OR + LIKE） */
    public static void applyTypeFilter(LambdaQueryWrapper<Film> wrapper, List<String> types) {
        if (types == null || types.isEmpty()) {
            return;
        }
        List<String> keywords = types.stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
        if (keywords.isEmpty()) {
            return;
        }
        wrapper.and(w -> {
            for (int i = 0; i < keywords.size(); i++) {
                String keyword = keywords.get(i);
                if (i == 0) {
                    w.like(Film::getType, keyword);
                } else {
                    w.or().like(Film::getType, keyword);
                }
            }
        });
    }
}
