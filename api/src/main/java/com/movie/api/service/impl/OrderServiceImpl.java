package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.movie.api.model.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.api.constant.OrderStatus;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.mapper.OrderMapper;
import com.movie.api.mapper.UserMapper;
import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.entity.Cart;
import com.movie.api.model.entity.Film;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.OrderVO;
import com.movie.api.model.vo.PageResult;
import com.movie.api.service.ArrangementService;
import com.movie.api.service.OrderService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.movie.api.utils.ArrangementScheduleUtil;
import com.movie.api.utils.DataTimeUtil;
import com.movie.api.utils.PayUtil;
import com.movie.api.utils.SeatUtil;
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

    @Resource
    private PayUtil payUtil;

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
        boolean cartLockActive = SeatUtil.isCartLockActive(cart.getCreateAt());
        String excludeCartId = cartLockActive ? cart.getId() : null;
        try {
            arrangementService.validateSeatsAvailable(cart.getAid(), cart.getSeats(), excludeCartId);
        } catch (Exception e) {
            if (!cartLockActive) {
                throw new Exception("放置购物车时间过长，座位已被他人选走，请重新选座");
            }
            throw e;
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
        order.setStatus(OrderStatus.PAYMENT_WAITING);
        order.setSeats(cart.getSeats());
        order.setPrice(cart.getPrice());
        // 结算时重新计时，支付窗口 5 分钟
        order.setCreateAt(DataTimeUtil.getNowTimeString());
        orderMapper.insert(order);

        String[] split = cart.getSeats().split("号");
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

        if (DataTimeUtil.parseTimeStamp(order.getCreateAt()) + OrderStatus.PAYMENT_EXPIRATION_TIME
                < System.currentTimeMillis()) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            orderMapper.updateById(order);
            throw new Exception("订单支付超时，请重新选座");
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

        if (DataTimeUtil.parseTimeStamp(order.getCreateAt()) + OrderStatus.PAYMENT_EXPIRATION_TIME
                < System.currentTimeMillis()) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            orderMapper.updateById(order);
            throw new Exception("订单支付超时，请重新选座");
        }

        order.setStatus(OrderStatus.PAYMENT_SUCCESSFUL);
        order.setPayAt(DataTimeUtil.getNowTimeString());
        orderMapper.updateById(order);
        return order;
    }

    @Override
    public Order refund(String id, String uid) throws Exception {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if (uid == null || !uid.equals(order.getUid())) {
            throw new Exception("无权操作该订单");
        }
        if (!OrderStatus.PAYMENT_SUCCESSFUL.equals(order.getStatus())) {
            throw new Exception("仅支付成功的订单可申请退款");
        }

        Arrangement arrangement = arrangementService.findById(order.getAid());
        if (arrangement == null) {
            throw new Exception("场次不存在");
        }
        if (!ArrangementScheduleUtil.isBeforeShowStart(arrangement)) {
            throw new Exception("电影已开场或放映时间已过，无法退款");
        }

        String refundAmount = String.format("%.2f", order.getPrice());
        String outRequestNo = order.getId() + "_refund";
        try {
            // 本地已校验支付成功，直接调用退款接口（避免沙箱 trade.query 频繁 504）
            AlipayTradeRefundResponse refundResponse = payUtil.refund(order.getId(), refundAmount, outRequestNo);
            if (!refundResponse.isSuccess()) {
                String msg = refundResponse.getSubMsg() != null ? refundResponse.getSubMsg() : refundResponse.getMsg();
                throw new Exception("支付宝退款失败：" + msg);
            }
        } catch (AlipayApiException e) {
            throw new Exception("支付宝退款失败：" + PayUtil.friendlyMessage(e));
        }

        order.setStatus(OrderStatus.REFUNDED);
        orderMapper.updateById(order);

        if (arrangement != null) {
            Film film = filmMapper.selectById(arrangement.getFid());
            if (film != null) {
                int seatCount = SeatUtil.parseSeatNumbers(order.getSeats()).size();
                film.setHot(Math.max(0, film.getHot() - seatCount));
                filmMapper.updateById(film);
            }
        }
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

    @Override
    public PageResult<OrderVO> findByPage(Integer page, Integer size, String keyword) {
        Page<OrderVO> pageParam = new Page<>(page, size);
        IPage<OrderVO> iPage = orderMapper.selectOrderDetailPage(pageParam, keyword);
        List<OrderVO> records = iPage.getRecords();
        long total = iPage.getTotal();
        return new PageResult<>(total, (int) iPage.getCurrent(), (int) iPage.getSize(), records);
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