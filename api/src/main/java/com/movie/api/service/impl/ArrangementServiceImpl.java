package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.movie.api.mapper.ArrangementMapper;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.mapper.OrderMapper;
import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.entity.Film;
import com.movie.api.model.entity.Order;
import com.movie.api.model.vo.ArrangementVO;
import com.movie.api.service.ArrangementService;
import com.movie.api.service.FilmService;
import com.movie.api.utils.ArrangementScheduleUtil;
import com.movie.api.utils.DataTimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ArrangementServiceImpl implements ArrangementService {

    @Resource
    private ArrangementMapper arrangementMapper;

    @Resource
    private FilmMapper filmMapper;

    @Resource
    private OrderMapper orderMapper;

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
        List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>().in("aid", id));
        List<Integer> seats = new ArrayList<>();
        for (Order o : orders) {
            String[] split = o.getSeats().split("号");
            for (String s : split) {
                seats.add(Integer.parseInt(s));
            }
        }
        return seats;
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

    /**
     * 按影片时长写入结束时间，并校验同一电影下场次区间 [开始, 结束) 互不重叠。
     */
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

}
