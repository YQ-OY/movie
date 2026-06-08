package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.movie.api.model.entity.User;
import com.movie.api.constant.OrderStatus;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.mapper.OrderMapper;
import com.movie.api.mapper.UserMapper;
import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.entity.Cart;
import com.movie.api.model.entity.Film;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.OrderVO;
import com.movie.api.service.ArrangementService;
import com.movie.api.service.OrderService;
import com.movie.api.utils.ArrangementScheduleUtil;
import com.movie.api.utils.DataTimeUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "order")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ArrangementService arrangementService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FilmMapper filmMapper;

    // 创建订单（从购物车生成订单，同时增加电影热度）
    @Override
    public Order create(Cart cart) throws Exception {
        Arrangement arrangement = arrangementService.findById(cart.getAid());
        if (arrangement == null) {
            throw new Exception("场次不存在");
        }
        if (!ArrangementScheduleUtil.isTicketSaleAllowed(arrangement)) {
            throw new Exception("开场前" + ArrangementScheduleUtil.TICKET_SALES_CLOSE_BEFORE_MINUTES + "分钟停止售票");
        }
        List<Integer> seats = arrangementService.getSeatsHaveSelected(cart.getAid());
        String[] split = cart.getSeats().split("号");
        for (String s : split) {
            if (seats.contains(Integer.parseInt(s))) throw new Exception("影片在购物车中躺了太长时间了，座位已被其他用户预订并支付了");
        }
        Order order = new Order();
        //生成订单id
        order.setId(UUID.randomUUID().toString());
        //写入用户id
        order.setUid(cart.getUid());
        //写入用户电话
        order.setPhone(cart.getPhone());
        //写入场次id
        order.setAid(cart.getAid());
        //写入座位信息
        order.setStatus(cart.getStatus());
        order.setSeats(cart.getSeats());
        if (cart.getStatus() == 2) order.setPayAt(DataTimeUtil.getNowTimeString());
        order.setPrice(cart.getPrice());
        order.setCreateAt(DataTimeUtil.getNowTimeString());
        orderMapper.insert(order);

        //订了几个座位就添加多少热度
        Film film = filmMapper.selectById(arrangement.getFid());
        film.setHot(film.getHot() + split.length);
        filmMapper.updateById(film);
        return order;
    }

    @Override
    public Order validateForAlipayPay(String orderId, String username) throws Exception {
        Order order = validateOrderForAlipayPay(orderId);

        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("username", username);
        User user = userMapper.selectOne(userWrapper);
        if (user == null || !user.getId().equals(order.getUid())) {
            throw new Exception("无权支付该订单");
        }

        return order;
    }

    @Override
    public Order validateOrderForAlipayPay(String orderId) throws Exception {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }

        if (!OrderStatus.PAYMENT_WAITING.equals(order.getStatus())) {
            throw new Exception("订单状态不允许支付");
        }

        Arrangement arrangement = arrangementService.findById(order.getAid());
        if (arrangement != null && !ArrangementScheduleUtil.isTicketSaleAllowed(arrangement)) {
            throw new Exception("开场前" + ArrangementScheduleUtil.TICKET_SALES_CLOSE_BEFORE_MINUTES + "分钟停止售票");
        }

        if (DataTimeUtil.parseTimeStamp(order.getCreateAt()) + OrderStatus.EXPIRATION_TIME
                < System.currentTimeMillis()) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            orderMapper.updateById(order);
            throw new Exception("订单支付超时");
        }

        return order;
    }

    @Override
    public void completeAlipayPayment(String orderId, String payAt) throws Exception {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if (OrderStatus.PAYMENT_SUCCESSFUL.equals(order.getStatus())) {
            return;
        }
        if (!OrderStatus.PAYMENT_WAITING.equals(order.getStatus())) {
            return;
        }
        order.setStatus(OrderStatus.PAYMENT_SUCCESSFUL);
        order.setPayAt(payAt != null && !payAt.isEmpty() ? payAt : DataTimeUtil.getNowTimeString());
        orderMapper.updateById(order);
    }

    @Override
    public String buildPaySubject(String orderId) throws Exception {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        Arrangement arrangement = arrangementService.findById(order.getAid());
        if (arrangement == null) {
            return "电影票订单-" + orderId.substring(0, 8);
        }
        Film film = filmMapper.selectById(arrangement.getFid());
        String filmName = film != null ? film.getName().trim() : "电影票";
        return filmName + " - " + order.getSeats().trim();
    }

    // 支付订单（校验超时和场次状态）
    @Override
    public Order pay(String id) throws Exception {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new Exception("不存在的订单id");

        Arrangement arrangement = arrangementService.findById(order.getAid());
        if (arrangement != null && !ArrangementScheduleUtil.isTicketSaleAllowed(arrangement)) {
            throw new Exception("开场前" + ArrangementScheduleUtil.TICKET_SALES_CLOSE_BEFORE_MINUTES + "分钟停止售票");
        }

        if (DataTimeUtil.parseTimeStamp(order.getCreateAt()) + OrderStatus.EXPIRATION_TIME
                < System.currentTimeMillis()) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            orderMapper.updateById(order);
            throw new Exception("订单支付超时");
        }

        order.setStatus(OrderStatus.PAYMENT_SUCCESSFUL);
        order.setPayAt(DataTimeUtil.getNowTimeString());
        orderMapper.updateById(order);
        return order;
    }

    // 更新订单信息
    @Override
    public void update(Order order) {
        orderMapper.updateById(order);
    }

    // 查询所有订单（含用户、电影、场次关联信息）
    @Override
    public List<OrderVO> findAll() {
        return findByWrapper(new QueryWrapper<>());
    }

    // 根据用户ID查询订单
    @Override
    public List<OrderVO> findByUser(String uid) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.in("uid", uid);
        return findByWrapper(wrapper);
    }

    // 根据条件查询订单并组装VO
    private List<OrderVO> findByWrapper(QueryWrapper<Order> wrapper) {
        List<Order> orders = orderMapper.selectList(wrapper);
        List<OrderVO> result = new ArrayList<>();
        for (Order o : orders) {
            OrderVO orderVO = new OrderVO();
            orderVO.setOrder(o);
            orderVO.setUser(userMapper.selectById(o.getUid()));
            Arrangement arrangement = arrangementService.findById(o.getAid());
            orderVO.setArrangement(arrangement);
            orderVO.setFilm(filmMapper.selectById(arrangement.getFid()));
            result.add(orderVO);
        }
        return result;
    }

}