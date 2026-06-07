package com.movie.api.service.impl;

import com.movie.api.constant.OrderStatus;
import com.movie.api.mapper.OrderExceptionMapper;
import com.movie.api.mapper.OrderMapper;
import com.movie.api.model.entity.Order;
import com.movie.api.model.entity.OrderException;
import com.movie.api.service.OrderExceptionService;
import com.movie.api.utils.DataTimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class OrderExceptionServiceImpl implements OrderExceptionService {

    @Resource
    private OrderExceptionMapper orderExceptionMapper;

    // 创建订单异常记录
    @Override
    public OrderException create(OrderException orderException) {
        orderException.setStatus(false);
        orderException.setId(UUID.randomUUID().toString());
        orderException.setCreateAt(DataTimeUtil.getNowTimeString());
        orderExceptionMapper.insert(orderException);
        return orderException;
    }

    // 查询所有订单异常
    @Override
    public List<OrderException> findAll() {
        return orderExceptionMapper.selectList(null);
    }

    // 处理订单异常（记录处理结束时间）
    @Override
    public void handleException(OrderException orderException) {
        orderException.setEndAt(DataTimeUtil.getNowTimeString());
        orderExceptionMapper.updateById(orderException);
    }

}