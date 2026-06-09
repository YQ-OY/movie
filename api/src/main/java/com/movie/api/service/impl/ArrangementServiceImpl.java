package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.api.constant.OrderStatus;
import com.movie.api.mapper.ArrangementMapper;
import com.movie.api.mapper.CartMapper;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.mapper.OrderMapper;
import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.entity.Cart;
import com.movie.api.model.entity.Film;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.ArrangementVO;
import com.movie.api.model.vo.PageResult;
import com.movie.api.model.vo.SeatStatusVO;
import com.movie.api.service.ArrangementService;
import com.movie.api.service.FilmService;
import com.movie.api.utils.ArrangementScheduleUtil;
import com.movie.api.utils.DataTimeUtil;
import com.movie.api.utils.SeatUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ArrangementServiceImpl implements ArrangementService {

    @Resource
    private ArrangementMapper arrangementMapper;

    @Resource
    private FilmMapper filmMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private FilmService filmService;

    @Override
    public void save(Arrangement arrangement) {
        fillEndTimeAndAssertNoOverlap(arrangement, null);
        arrangement.setBoxOffice(0);
        arrangement.setCreateAt(DataTimeUtil.getNowTimeString());
        arrangementMapper.insert(arrangement);
    }

    @Override
    public List<Arrangement> findAll() {
        return arrangementMapper.selectList(null);
    }

    @Override
    public ArrangementVO findByFilmId(String fid) {
        List<Arrangement> list = arrangementMapper.selectList(new QueryWrapper<Arrangement>().in("fid", fid));
        return new ArrangementVO(list, filmMapper.selectById(fid));
    }

    @Override
    public List<Integer> getSeatsHaveSelected(Long id) {
        return getSeatStatus(id).getSoldSeats();
    }

    @Override
    public SeatStatusVO getSeatStatus(Long id) {
        Set<Integer> sold = new HashSet<>();
        QueryWrapper<Order> paidWrapper = new QueryWrapper<>();
        paidWrapper.eq("aid", id).eq("status", OrderStatus.PAYMENT_SUCCESSFUL);
        for (Order order : orderMapper.selectList(paidWrapper)) {
            sold.addAll(SeatUtil.parseSeatNumbers(order.getSeats()));
        }

        Set<Integer> locked = new HashSet<>();
        QueryWrapper<Cart> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("aid", id);
        for (Cart cart : cartMapper.selectList(cartWrapper)) {
            if (SeatUtil.isCartLockActive(cart.getCreateAt())) {
                locked.addAll(SeatUtil.parseSeatNumbers(cart.getSeats()));
            }
        }

        QueryWrapper<Order> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("aid", id).eq("status", OrderStatus.PAYMENT_WAITING);
        for (Order order : orderMapper.selectList(pendingWrapper)) {
            if (SeatUtil.isOrderLockActive(order.getCreateAt())) {
                locked.addAll(SeatUtil.parseSeatNumbers(order.getSeats()));
            }
        }

        locked.removeAll(sold);

        SeatStatusVO vo = new SeatStatusVO();
        vo.setSoldSeats(new ArrayList<>(sold));
        vo.setLockedSeats(new ArrayList<>(locked));
        return vo;
    }

    @Override
    public void validateSeatsAvailable(Long arrangementId, String seats, String excludeCartId) throws Exception {
        SeatStatusVO status = getSeatStatus(arrangementId);
        Set<Integer> unavailable = new HashSet<>();
        unavailable.addAll(status.getSoldSeats());
        unavailable.addAll(status.getLockedSeats());

        if (excludeCartId != null && !excludeCartId.isEmpty()) {
            Cart cart = cartMapper.selectById(excludeCartId);
            if (cart != null && Objects.equals(cart.getAid(), arrangementId)) {
                unavailable.removeAll(SeatUtil.parseSeatNumbers(cart.getSeats()));
            }
        }

        for (Integer seat : SeatUtil.parseSeatNumbers(seats)) {
            if (unavailable.contains(seat)) {
                throw new Exception("座位 " + seat + " 号已被预订或锁座中，请选择其他座位");
            }
        }
    }

    @Override
    public Arrangement findById(Long id) {
        return arrangementMapper.selectById(id);
    }

    @Override
    public void deleteById(Long id) {
        arrangementMapper.deleteById(id);
    }

    @Override
    public Arrangement Update(Arrangement arrangement) {
        fillEndTimeAndAssertNoOverlap(arrangement, arrangement.getId());
        arrangementMapper.updateById(arrangement);
        return arrangement;
    }

    private void fillEndTimeAndAssertNoOverlap(Arrangement a, Long excludeId) {
        if (a.getFid() == null || a.getFid().trim().isEmpty()) {
            throw new IllegalArgumentException("请选择电影");
        }
        if (a.getDate() == null || a.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("请选择放映日期");
        }
        if (a.getStartTime() == null || a.getStartTime().trim().isEmpty()) {
            throw new IllegalArgumentException("请选择开始时间");
        }
        Film film = filmService.findById(a.getFid().trim());
        if (film == null) {
            throw new IllegalArgumentException("电影不存在");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new IllegalArgumentException("影片未设置有效时长（分钟），请先在电影管理中维护");
        }
        LocalDateTime start = ArrangementScheduleUtil.startDateTime(a.getDate(), a.getStartTime());
        LocalDateTime endExclusive = ArrangementScheduleUtil.exclusiveEndFromStartAndDuration(start, film.getDuration());
        a.setEndTime(ArrangementScheduleUtil.formatHms(endExclusive));

        List<Arrangement> siblings = arrangementMapper.selectList(
                new QueryWrapper<Arrangement>().eq("fid", a.getFid().trim()));
        for (Arrangement other : siblings) {
            if (excludeId != null && other.getId() != null && Objects.equals(other.getId(), excludeId)) {
                continue;
            }
            LocalDateTime os = ArrangementScheduleUtil.startDateTime(other.getDate(), other.getStartTime());
            LocalDateTime oe = ArrangementScheduleUtil.exclusiveEndForArrangement(other);
            if (ArrangementScheduleUtil.intervalsOverlap(start, endExclusive, os, oe)) {
                throw new IllegalArgumentException("同一电影下场次时间不能重叠，请调整放映日期或开始时间");
            }
        }
    }

    @Override
    public PageResult<Arrangement> findByPage(Integer page, Integer size, String name, String startDate, String endDate) {
        LambdaQueryWrapper<Arrangement> wrapper = new LambdaQueryWrapper<>();

        // 电影名称模糊查询
        if (StringUtils.hasText(name)) {
            wrapper.like(Arrangement::getName, name);
        }
        // 放映日期区间查询
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(Arrangement::getDate, startDate);
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(Arrangement::getDate, endDate);
        }
        // 按放映日期 + 开始时间排序
        wrapper.orderByAsc(Arrangement::getDate, Arrangement::getStartTime);

        Page<Arrangement> pageParam = new Page<>(page, size);
        Page<Arrangement> arrangementPage = arrangementMapper.selectPage(pageParam, wrapper);

        return new PageResult<>(
                arrangementPage.getTotal(),
                (int) arrangementPage.getCurrent(),
                (int) arrangementPage.getSize(),
                arrangementPage.getRecords()
        );
    }
}
