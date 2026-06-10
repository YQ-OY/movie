package com.movie.api.controller;

import com.movie.api.annotation.DisableBaseResponse;
import com.movie.api.config.AliPayConfig;
import com.movie.api.model.entity.Order;
import com.movie.api.service.OrderService;
import com.movie.api.utils.PayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alipay")
public class AliPayController {

    private static final Logger log = LoggerFactory.getLogger(AliPayController.class);

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private PayUtil payUtil;

    @Resource
    private OrderService orderService;

    @GetMapping("/pay")
    @DisableBaseResponse
    public void pay(@RequestParam String orderId, HttpServletResponse response) throws Exception {
        Order order = orderService.validateOrderForAlipayPay(orderId);
        String subject = orderService.buildPaySubject(orderId);
        String totalAmount = String.format("%.2f", order.getPrice());
        String form = payUtil.createPagePayForm(orderId, totalAmount, subject);

        response.setContentType("text/html;charset=" + aliPayConfig.getCharset());
        response.getWriter().write(form);
        response.getWriter().flush();
    }

    /**
     * 支付宝同步回跳：验签后主动查询交易状态并更新本地订单，再重定向到前端结果页。
     * 解决本地/沙箱环境下异步 notify 无法送达导致订单状态不更新的问题。
     */
    @GetMapping("/return")
    @DisableBaseResponse
    public void payReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = convertRequestParams(request);
        String outTradeNo = params.get("out_trade_no");
        String frontend = resolveFrontendReturnUrl();
        try {
            if (outTradeNo == null || outTradeNo.isBlank()) {
                response.sendRedirect(frontend + "?error=missing_order");
                return;
            }
            if (!payUtil.verifyNotify(params)) {
                log.warn("支付宝同步回跳验签失败, out_trade_no={}", outTradeNo);
                response.sendRedirect(buildFrontendRedirect(frontend, outTradeNo, params.get("total_amount"), "sign"));
                return;
            }
            orderService.syncAlipayPaymentStatus(outTradeNo);
            log.info("支付宝同步回跳确认成功, out_trade_no={}", outTradeNo);
            response.sendRedirect(buildFrontendRedirect(frontend, outTradeNo, params.get("total_amount"), null));
        } catch (Exception e) {
            log.error("处理支付宝同步回跳失败, out_trade_no={}", outTradeNo, e);
            response.sendRedirect(buildFrontendRedirect(frontend, outTradeNo, params.get("total_amount"), "sync"));
        }
    }

    /** 前端支付结果页可主动调用，向支付宝查询并同步订单状态（幂等） */
    @GetMapping("/confirm")
    public Order confirm(@RequestParam String orderId) throws Exception {
        return orderService.syncAlipayPaymentStatus(orderId);
    }

    @PostMapping("/notify")
    @DisableBaseResponse
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = convertRequestParams(request);
        try {
            if (!payUtil.verifyNotify(params)) {
                log.warn("支付宝异步通知验签失败, out_trade_no={}", params.get("out_trade_no"));
                response.getWriter().write("failure");
                return;
            }

            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                String outTradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                orderService.completeAlipayPayment(outTradeNo, gmtPayment);
                log.info("支付宝支付成功, out_trade_no={}, trade_no={}", outTradeNo, params.get("trade_no"));
            }
            response.getWriter().write("success");
        } catch (Exception e) {
            log.error("处理支付宝异步通知失败", e);
            response.getWriter().write("failure");
        }
    }

    private Map<String, String> convertRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null && values.length > 0) {
                params.put(name, String.join(",", values));
            }
        }
        return params;
    }

    private String resolveFrontendReturnUrl() {
        String frontend = aliPayConfig.getFrontendReturnUrl();
        if (frontend == null || frontend.isBlank()) {
            return "http://localhost:8080/pay/success";
        }
        return frontend.trim();
    }

    private String buildFrontendRedirect(String frontend, String outTradeNo, String totalAmount, String error)
            throws IOException {
        StringBuilder url = new StringBuilder(frontend);
        url.append(frontend.contains("?") ? "&" : "?");
        url.append("out_trade_no=").append(URLEncoder.encode(outTradeNo, StandardCharsets.UTF_8));
        if (totalAmount != null && !totalAmount.isBlank()) {
            url.append("&total_amount=").append(URLEncoder.encode(totalAmount, StandardCharsets.UTF_8));
        }
        if (error != null && !error.isBlank()) {
            url.append("&error=").append(URLEncoder.encode(error, StandardCharsets.UTF_8));
        }
        return url.toString();
    }
}
