package com.movie.api.service;

import com.movie.api.model.entity.OrderException;
import com.movie.api.model.vo.PageResult;

import java.util.List;

public interface OrderExceptionService {

    OrderException create(OrderException orderException);

    List<OrderException> findAll();

    void handleException(OrderException orderException);

    PageResult<OrderException> findByPage(Integer page, Integer size, String keyword);
}
