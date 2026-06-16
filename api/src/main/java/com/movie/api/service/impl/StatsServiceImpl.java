package com.movie.api.service.impl;

import com.movie.api.constant.OrderStatus;
import com.movie.api.mapper.ArrangementMapper;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.mapper.UserMapper;
import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.entity.Film;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.DashboardStatsVO;
import com.movie.api.model.vo.HeatmapItemVO;
import com.movie.api.model.vo.OrderVO;
import com.movie.api.model.vo.StatItemVO;
import com.movie.api.service.OrderService;
import com.movie.api.service.StatsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Resource
    private ArrangementMapper arrangementMapper;

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
        vo.setRevenueTotal(round2(revenue));

        vo.setUserTotal(userMapper.selectCount(null));
        vo.setFilmTotal(filmMapper.selectCount(null));

        long todayOrderTotal = orders.stream()
                .filter(o -> isToday(o.getCreateAt()))
                .count();
        double todayRevenueTotal = orders.stream()
                .filter(o -> isToday(o.getCreateAt()))
                .filter(o -> OrderStatus.PAYMENT_SUCCESSFUL.equals(o.getStatus()))
                .mapToDouble(Order::getPrice)
                .sum();
        vo.setTodayOrderTotal(todayOrderTotal);
        vo.setTodayRevenueTotal(round2(todayRevenueTotal));

        long todayArrangementTotal = arrangementMapper.selectList(null).stream()
                .filter(a -> isToday(a.getCreateAt()))
                .count();
        vo.setTodayArrangementTotal(todayArrangementTotal);

        long totalSuccess = vo.getOrderPaid();
        long totalFailed = vo.getOrderFailed();
        long totalPending = vo.getOrderPending();
        double conversion = totalPending + totalFailed + totalSuccess == 0 ? 0 : (totalSuccess * 100.0 / (totalPending + totalFailed + totalSuccess));
        vo.setTodayConversionRate(round2(conversion));

        vo.getOrderStatusPie().add(new StatItemVO("待支付", vo.getOrderPending()));
        vo.getOrderStatusPie().add(new StatItemVO("支付失败", vo.getOrderFailed()));
        vo.getOrderStatusPie().add(new StatItemVO("已支付", vo.getOrderPaid()));
        vo.getOrderStatusPie().add(new StatItemVO("已撤销", vo.getOrderCancelled()));
        vo.getOrderStatusPie().add(new StatItemVO("已退款", countStatus(orders, OrderStatus.REFUNDED)));

        Map<String, Double> filmSum = new LinkedHashMap<>();
        Map<String, String> filmTypeMap = new LinkedHashMap<>();
        for (OrderVO ov : all) {
            Order o = ov.getOrder();
            if (o == null || !OrderStatus.PAYMENT_SUCCESSFUL.equals(o.getStatus())) {
                continue;
            }
            Film f = ov.getFilm();
            String name = f != null && f.getName() != null ? f.getName() : "未知影片";
            filmSum.merge(name, o.getPrice(), Double::sum);
            if (f != null && f.getType() != null) {
                filmTypeMap.putIfAbsent(name, f.getType());
            }
        }
        List<StatItemVO> top = filmSum.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(e -> new StatItemVO(e.getKey(), round2(e.getValue())))
                .collect(Collectors.toList());
        vo.setTopFilmsByRevenue(top);

        Map<String, Double> typeRevenue = new LinkedHashMap<>();
        for (OrderVO ov : all) {
            Order o = ov.getOrder();
            Film f = ov.getFilm();
            if (o == null || f == null || !OrderStatus.PAYMENT_SUCCESSFUL.equals(o.getStatus())) {
                continue;
            }
            String type = normalizeType(f.getType());
            typeRevenue.merge(type, o.getPrice(), Double::sum);
        }
        List<StatItemVO> typeList = typeRevenue.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(e -> new StatItemVO(e.getKey(), round2(e.getValue())))
                .collect(Collectors.toList());
        vo.setRevenueByFilmType(typeList);

        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        Map<String, Long> dayMap = new LinkedHashMap<>();
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            String day = today.minusDays(i).format(fmt);
            dayMap.put(day, 0L);
            revenueMap.put(day, 0.0);
        }
        for (Order o : orders) {
            String day = extractDay(o.getCreateAt());
            if (!dayMap.containsKey(day)) continue;
            dayMap.merge(day, 1L, Long::sum);
            if (OrderStatus.PAYMENT_SUCCESSFUL.equals(o.getStatus())) {
                revenueMap.merge(day, o.getPrice(), Double::sum);
            }
        }
        List<StatItemVO> ordersSeries = new ArrayList<>();
        List<StatItemVO> revenueSeries = new ArrayList<>();
        for (Map.Entry<String, Long> e : dayMap.entrySet()) {
            ordersSeries.add(new StatItemVO(e.getKey(), e.getValue()));
            revenueSeries.add(new StatItemVO(e.getKey(), round2(revenueMap.get(e.getKey()))));
        }
        vo.setOrdersByDay(ordersSeries);
        vo.setRevenueByDay(revenueSeries);

        Map<String, Integer> heatMap = new LinkedHashMap<>();
        for (int h = 0; h < 24; h++) {
            heatMap.put(String.format("%02d:00", h), 0);
        }
        for (Order o : orders) {
            if (!isToday(o.getCreateAt())) continue;
            String hour = extractHour(o.getCreateAt());
            if (hour != null && heatMap.containsKey(hour)) {
                heatMap.merge(hour, 1, Integer::sum);
            }
        }
        List<HeatmapItemVO> heatmap = new ArrayList<>();
        for (Map.Entry<String, Integer> e : heatMap.entrySet()) {
            heatmap.add(new HeatmapItemVO(e.getKey(), e.getValue()));
        }
        vo.setHourlyTicketHeatmap(heatmap);

        vo.getPurchaseFunnel().add(new StatItemVO("浏览影片", vo.getFilmTotal()));
        vo.getPurchaseFunnel().add(new StatItemVO("进入场次", vo.getTodayArrangementTotal()));
        vo.getPurchaseFunnel().add(new StatItemVO("创建订单", vo.getTodayOrderTotal()));
        vo.getPurchaseFunnel().add(new StatItemVO("完成支付", vo.getOrderPaid()));

        return vo;
    }

    private static long countStatus(List<Order> orders, Integer status) {
        return orders.stream().filter(o -> o.getStatus() != null && o.getStatus().equals(status)).count();
    }

    private static boolean isToday(String createAt) {
        return createAt != null && createAt.length() >= 10 && LocalDate.now().toString().equals(createAt.substring(0, 10));
    }

    private static String extractDay(String createAt) {
        return createAt != null && createAt.length() >= 10 ? createAt.substring(0, 10) : "";
    }

    private static String extractHour(String createAt) {
        try {
            if (createAt == null || createAt.length() < 13) return null;
            LocalDateTime dt = LocalDateTime.parse(createAt.substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dt.toLocalTime().withMinute(0).withSecond(0).withNano(0).format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            if (createAt.length() >= 13) {
                return createAt.substring(11, 13) + ":00";
            }
            return null;
        }
    }

    private static String normalizeType(String type) {
        return (type == null || type.trim().isEmpty()) ? "其他" : type.trim();
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
