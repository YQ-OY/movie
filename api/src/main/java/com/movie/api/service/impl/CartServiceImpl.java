package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.movie.api.mapper.ArrangementMapper;
import com.movie.api.mapper.CartMapper;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.entity.Cart;
import com.movie.api.model.entity.Film;
import com.movie.api.model.vo.CartVO;
import com.movie.api.service.ArrangementService;
import com.movie.api.service.CartService;
import com.movie.api.service.OrderService;
import com.movie.api.utils.ArrangementScheduleUtil;
import com.movie.api.utils.DataTimeUtil;
import com.movie.api.utils.ValidationUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "cart")
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private OrderService orderService;

    @Resource
    private ArrangementMapper arrangementMapper;

    @Resource
    private FilmMapper filmMapper;

    @Resource
    private ArrangementService arrangementService;

    // 添加商品到购物车
    @Override
    public void save(Cart cart) throws Exception {
        ValidationUtil.requireValidMobileCN(cart.getPhone(), "手机号码");
        Arrangement ar = arrangementMapper.selectById(cart.getAid());
        if (ar == null) {
            throw new Exception("场次不存在");
        }
        if (!ArrangementScheduleUtil.isTicketSaleAllowed(ar)) {
            throw new Exception("开场前" + ArrangementScheduleUtil.TICKET_SALES_CLOSE_BEFORE_MINUTES + "分钟停止售票");
        }
        arrangementService.validateSeatsAvailable(cart.getAid(), cart.getSeats(), null);
        cart.setCreateAt(DataTimeUtil.getNowTimeString());
        cartMapper.insert(cart);
    }

    // 根据ID删除购物车项
    @Override
    @CacheEvict
    public void deleteById(String id) {
        cartMapper.deleteById(id);
    }

    // 清空用户所有购物车项
    @Override
    @CacheEvict
    public void deleteAllByUserId(String uid) {
        cartMapper.delete(new QueryWrapper<Cart>().in("uid", uid));
    }

    // 查询用户的所有购物车项（含电影和场次详情）
    @Override
    @Cacheable
    public List<CartVO> findAllByUserId(String uid) {
        List<CartVO> result = new ArrayList<>();
        QueryWrapper<Cart> wrapper = new QueryWrapper<Cart>().in("uid", uid);
        wrapper.orderByDesc("create_at");
        List<Cart> carts = cartMapper.selectList(wrapper);
        for (Cart c : carts) {
            Arrangement arrangement = arrangementMapper.selectById(c.getAid());
            Film film = filmMapper.selectById(arrangement.getFid());
            CartVO cartVO = new CartVO(film, arrangement, c);
            result.add(cartVO);
        }
        return result;
    }

    // 批量删除购物车项
    @Override
    public void deleteCarts(List<Cart> carts) {
        for (Cart c : carts) {
            cartMapper.deleteById(c.getId());
        }
    }

    // 结算购物车（将购物车项转为订单）
    @Override
    public void settleCarts(List<Cart> carts) throws Exception {
        for (Cart c : carts) {
            orderService.create(c);
        }
    }

}