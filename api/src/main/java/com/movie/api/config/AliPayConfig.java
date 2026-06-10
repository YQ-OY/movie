package com.movie.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    /** 支付宝同步回跳地址（后端接口，用于验签并更新订单） */
    private String returnUrl;
    /** 同步处理完成后重定向到前端的支付结果页 */
    private String frontendReturnUrl;
    private String gatewayUrl;
    private String signType;
    private String charset;
    private String format;

    @PostConstruct
    public void normalize() {
        appPrivateKey = stripKey(appPrivateKey);
        alipayPublicKey = stripKey(alipayPublicKey);
        charset = "UTF-8";
        if (format == null || format.isBlank()) {
            format = "json";
        }
        if (signType == null || signType.isBlank()) {
            signType = "RSA2";
        }
    }

    private static String stripKey(String key) {
        if (key == null) {
            return null;
        }
        return key.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }
}

