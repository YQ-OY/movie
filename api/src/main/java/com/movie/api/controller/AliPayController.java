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
}
