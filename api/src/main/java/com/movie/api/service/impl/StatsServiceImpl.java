package com.movie.api.service.impl;

import com.movie.api.constant.OrderStatus;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.mapper.UserMapper;
import com.movie.api.model.entity.Film;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.DashboardStatsVO;
import com.movie.api.model.vo.OrderVO;
import com.movie.api.model.vo.StatItemVO;
import com.movie.api.service.OrderService;
import com.movie.api.service.StatsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    @Resource
    private OrderService orderService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FilmMapper filmMapper;

    // 构建仪表盘统计数据（订单统计、营收、影片排行、每日订单趋势）
    @Override
    public DashboardStatsVO buildDashboard() {
        DashboardStatsVO vo = new DashboardStatsVO();
        List<OrderVO> all = orderService.findAll();
        List<Order> orders = all.stream().map(OrderVO::getOrder).filter(o -> o != null).collect(Collectors.toList());

        vo.setOrderTotal(orders.size());
        vo.setOrderPaid(countStatus(orders, OrderStatus.PAYMENT_SUCCESSFUL));
        vo.setOrderPending(countStatus(orders, OrderStatus.PAYMENT_WAITING));
        vo.setOrderFailed(countStatus(orders, OrderStatus.PAYMENT_FAILED));
        vo.setOrderCancelled(countStatus(orders, OrderStatus.COUNTERMAND));

        double revenue = orders.stream()
                .filter(o -> OrderStatus.PAYMENT_SUCCESSFUL.equals(o.getStatus()))
                .mapToDouble(Order::getPrice)
                .sum();
        vo.setRevenueTotal(Math.round(revenue * 100) / 100.0);

        vo.setUserTotal(userMapper.selectCount(null));
        vo.setFilmTotal(filmMapper.selectCount(null));

        vo.getOrderStatusPie().add(new StatItemVO("待支付", vo.getOrderPending()));
        vo.getOrderStatusPie().add(new StatItemVO("支付失败", vo.getOrderFailed()));
        vo.getOrderStatusPie().add(new StatItemVO("已支付", vo.getOrderPaid()));
        vo.getOrderStatusPie().add(new StatItemVO("已撤销", vo.getOrderCancelled()));

        Map<String, Double> filmSum = new LinkedHashMap<>();
        for (OrderVO ov : all) {
            Order o = ov.getOrder();
            if (o == null || !OrderStatus.PAYMENT_SUCCESSFUL.equals(o.getStatus())) {
                continue;
            }
            Film f = ov.getFilm();
            String name = f != null && f.getName() != null ? f.getName() : "未知影片";
            filmSum.merge(name, o.getPrice(), Double::sum);
        }
        List<StatItemVO> top = filmSum.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(8)
                .map(e -> new StatItemVO(e.getKey(), Math.round(e.getValue() * 100) / 100.0))
                .collect(Collectors.toList());
        vo.setTopFilmsByRevenue(top);

        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        Map<String, Long> dayMap = new LinkedHashMap<>();
        for (int i = 13; i >= 0; i--) {
            dayMap.put(today.minusDays(i).format(fmt), 0L);
        }
        for (Order o : orders) {
            if (o.getCreateAt() == null || o.getCreateAt().length() < 10) {
                continue;
            }
            String day = o.getCreateAt().substring(0, 10);
            dayMap.merge(day, 1L, Long::sum);
        }
        List<StatItemVO> series = new ArrayList<>();
        for (Map.Entry<String, Long> e : dayMap.entrySet()) {
            series.add(new StatItemVO(e.getKey(), e.getValue()));
        }
        vo.setOrdersByDay(series);

        return vo;
    }

    // 统计指定状态的订单数量
    private static long countStatus(List<Order> orders, Integer status) {
        return orders.stream().filter(o -> o.getStatus() != null && o.getStatus().equals(status)).count();
    }
}