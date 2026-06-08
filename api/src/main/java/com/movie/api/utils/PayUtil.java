package com.movie.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.movie.api.config.AliPayConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class PayUtil {

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
        return alipayClient.execute(request);
    }
}
