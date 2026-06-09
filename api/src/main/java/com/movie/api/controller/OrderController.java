package com.movie.api.controller;

import com.movie.api.model.entity.Cart;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.OrderVO;
import com.movie.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Tag(name = "订单接口")
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("")
    @Operation(summary = "创建订单")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public Order save(@RequestBody Cart cart) throws Exception {
        return orderService.create(cart);
    }

    @GetMapping("")
    @Operation(summary = "查询所有订单（管理端/订单管理权限；客服端 vue-worker 信息统计仅 ROLE_WORKER 只读）")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORDER_MANAGE','ROLE_WORKER')")
    public List<OrderVO> findAll() {
        return orderService.findAll();
    }

    @PutMapping("")
    @Operation(summary = "更新订单")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORDER_MANAGE')")
    public void update(@RequestBody Order order) {
        orderService.update(order);
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "查询用户订单")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_ORDER_MANAGE')")
    public List<OrderVO> findByUser(@PathVariable String id) {
        return orderService.findByUser(id);
    }

    @GetMapping("/pay")
    @Operation(summary = "支付订单")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public Order save(String id) throws Exception {
        return orderService.pay(id);
    }

    @PostMapping("/refund")
    @Operation(summary = "用户申请退款")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public Order refund(@RequestParam String id, @RequestParam String uid) throws Exception {
        return orderService.refund(id, uid);
    }

}
