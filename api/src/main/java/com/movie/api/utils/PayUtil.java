package com.movie.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.movie.api.config.AliPayConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class PayUtil {

    private static final Logger log = LoggerFactory.getLogger(PayUtil.class);

    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 45000;
    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 1500;

    @Resource
    private AliPayConfig aliPayConfig;

    private AlipayClient alipayClient;

    @PostConstruct
    public void init() throws AlipayApiException {
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(aliPayConfig.getGatewayUrl());
        config.setAppId(aliPayConfig.getAppId());
        config.setPrivateKey(aliPayConfig.getAppPrivateKey());
        config.setFormat(aliPayConfig.getFormat());
        config.setCharset(aliPayConfig.getCharset());
        config.setAlipayPublicKey(aliPayConfig.getAlipayPublicKey());
        config.setSignType(aliPayConfig.getSignType());
        config.setConnectTimeout(CONNECT_TIMEOUT_MS);
        config.setReadTimeout(READ_TIMEOUT_MS);
        alipayClient = new DefaultAlipayClient(config);
    }

    public String createPagePayForm(String outTradeNo, String totalAmount, String subject) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("total_amount", totalAmount);
        bizContent.put("subject", subject.trim());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        return alipayClient.pageExecute(request).getBody();
    }

    public boolean verifyNotify(Map<String, String> params) throws AlipayApiException {
        return AlipaySignature.rsaCheckV1(
                params,
                aliPayConfig.getAlipayPublicKey(),
                aliPayConfig.getCharset(),
                aliPayConfig.getSignType()
        );
    }

    public AlipayTradeQueryResponse query(String outTradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        request.setBizContent(bizContent.toString());
        return executeWithRetry(request);
    }

    /**
     * 支付宝沙箱全额退款（带重试）。本地订单已支付成功时直接退款，不再强依赖 trade.query。
     */
    public AlipayTradeRefundResponse refund(String outTradeNo, String refundAmount, String outRequestNo)
            throws AlipayApiException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("refund_amount", refundAmount);
        bizContent.put("out_request_no", outRequestNo);
        request.setBizContent(bizContent.toString());
        return executeWithRetry(request);
    }

    /** 将网关异常转为用户可读提示 */
    public static String friendlyMessage(Throwable e) {
        if (e == null) {
            return "支付宝请求失败";
        }
        String msg = e.getMessage();
        if (msg == null) {
            return "支付宝请求失败";
        }
        if (msg.contains("504") || msg.contains("Gateway Time-out") || msg.contains("Gateway Timeout")) {
            return "支付宝沙箱网关超时，请稍后重试";
        }
        if (msg.contains("<!DOCTYPE") || msg.contains("<html")) {
            return "支付宝沙箱暂时无法访问，请稍后重试";
        }
        if (msg.length() > 200) {
            return msg.substring(0, 200) + "...";
        }
        return msg;
    }

    private <T extends AlipayResponse> T executeWithRetry(com.alipay.api.AlipayRequest<T> request)
            throws AlipayApiException {
        AlipayApiException last = null;
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                return alipayClient.execute(request);
            } catch (AlipayApiException e) {
                last = e;
                if (!isRetryable(e) || attempt == MAX_RETRY) {
                    throw e;
                }
                log.warn("支付宝请求失败，第 {} 次重试: {}", attempt, friendlyMessage(e));
                sleepQuietly(RETRY_DELAY_MS * attempt);
            }
        }
        throw last;
    }

    private static boolean isRetryable(AlipayApiException e) {
        String msg = e.getMessage();
        if (msg == null) {
            return false;
        }
        return msg.contains("504")
                || msg.contains("Gateway Time-out")
                || msg.contains("Gateway Timeout")
                || msg.contains("timed out")
                || msg.contains("Timeout")
                || msg.contains("<!DOCTYPE")
                || msg.contains("Connection reset")
                || msg.contains("connect");
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
