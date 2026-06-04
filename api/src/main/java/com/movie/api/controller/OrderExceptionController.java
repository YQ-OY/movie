package com.movie.api.controller;

import com.movie.api.model.entity.OrderException;
import com.movie.api.service.OrderExceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Tag(name = "订单异常上报接口")
@RequestMapping("/api/oe")
public class OrderExceptionController {

    @Resource
    private OrderExceptionService orderExceptionService;

    @PostMapping("")
    @Operation(summary = "添加异常订单")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORDER_MANAGE')")
    public OrderException create(@RequestBody OrderException orderException) {
        return orderExceptionService.create(orderException);
    }

    @GetMapping("")
    @Operation(summary = "查询所有异常订单")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORDER_MANAGE')")
    public List<OrderException> findAll() {
        return orderExceptionService.findAll();
    }

    @PutMapping("")
    @Operation(summary = "工作人员处理异常订单")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORDER_MANAGE')")
    public void handle(@RequestBody OrderException orderException) {
        orderExceptionService.handleException(orderException);
    }

}
