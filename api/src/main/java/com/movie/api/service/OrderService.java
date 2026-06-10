package com.movie.api.service;

import com.movie.api.model.entity.Cart;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.OrderVO;
import com.movie.api.model.vo.PageResult;

import java.util.List;

public interface OrderService {

    Order create(Cart cart) throws Exception;

    Order pay(String id) throws Exception;

    Order validateForAlipayPay(String orderId, String username) throws Exception;

    Order validateOrderForAlipayPay(String orderId) throws Exception;

    void completeAlipayPayment(String orderId, String payAt) throws Exception;

    Order syncAlipayPaymentStatus(String orderId) throws Exception;

    String buildPaySubject(String orderId) throws Exception;

    void update(Order order);

    List<OrderVO> findAll();

    List<OrderVO> findByUser(String uid);

    PageResult<OrderVO> findByPage(Integer page, Integer size, String keyword);

    Order refund(String id, String uid) throws Exception;

    Order adminRefund(String id) throws Exception;

}
