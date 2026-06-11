package com.movie.api.utils;

import java.util.regex.Pattern;

/**
 * 手机号等格式校验（与前端 {@code validate.js} 规则保持一致）
 */
public final class ValidationUtil {

    private static final Pattern MOBILE_CN = Pattern.compile("^1[3-9]\\d{9}$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidMobileCN(String phone) {
        return phone != null && MOBILE_CN.matcher(phone.trim()).matches();
    }

    /**
     * 中国大陆 11 位手机号（必填）
     */
    public static void requireValidMobileCN(String phone, String fieldLabel) throws Exception {
        if (isBlank(phone)) {
            throw new Exception(fieldLabel + "不能为空");
        }
        if (!isValidMobileCN(phone)) {
            throw new Exception("请输入正确的11位中国大陆手机号");
        }
    }

    /**
     * 手机号可选：为空不校验；有内容则须为合法中国大陆 11 位手机号
     */
    public static void requireValidMobileCNIfPresent(String phone, String fieldLabel) throws Exception {
        if (isBlank(phone)) {
            return;
        }
        if (!isValidMobileCN(phone)) {
            throw new Exception(fieldLabel + "格式不正确");
        }
    }

    public static String normalizePhone(String phone) {
        return phone == null ? null : phone.trim();
    }
}
