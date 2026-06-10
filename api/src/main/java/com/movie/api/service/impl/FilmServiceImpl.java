package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.model.entity.Film;
import com.movie.api.model.vo.PageResult;
import com.movie.api.service.FilmService;
import com.movie.api.utils.FilmReleaseUtil;
import com.movie.api.utils.FilmTypeUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@CacheConfig(cacheNames = "film")
public class FilmServiceImpl implements FilmService {

    @Resource
    private FilmMapper filmMapper;

    // 保存电影，默认热度为0
    @Override
    public void save(Film film) {
        film.setHot(0);
        normalizeFilmFields(film);
        filmMapper.insert(film);
    }

    // 根据ID删除电影
    @CacheEvict
    @Override
    public void deleteById(String id) {
        filmMapper.deleteById(id);
    }

    // 查询所有电影
    @Cacheable
    @Override
    public List<Film> findAll() {
        return filmMapper.selectList(null);
    }

    // 分页查询电影
    @Override
    public PageResult<Film> findByPage(Integer page, Integer size, String name,
                                       List<String> type, List<String> region, Boolean status,
                                       String releasePhase) {
        // 构建查询条件
        LambdaQueryWrapper<Film> wrapper = new LambdaQueryWrapper<>();

        // 1. 电影名称模糊查询（不为空时）
        if (StringUtils.hasText(name)) {
            wrapper.like(Film::getName, name);
        }

        // 2. 类型多选模糊查询（任一类型命中即匹配）
        FilmTypeUtil.applyTypeFilter(wrapper, type);

        // 3. 地区多选（region 列表不为空时）
        if (region != null && !region.isEmpty()) {
            wrapper.in(Film::getRegion, region);
        }

        // 4. 上架状态（不为 null 时）
        if (status != null) {
            wrapper.eq(Film::isStatus, status);
        }

        // 5. 上映阶段：showing / upcoming
        if (StringUtils.hasText(releasePhase)) {
            String today = FilmReleaseUtil.todayKey();
            wrapper.isNotNull(Film::getReleaseTime).ne(Film::getReleaseTime, "");
            if ("showing".equalsIgnoreCase(releasePhase)) {
                wrapper.le(Film::getReleaseTime, today);
            } else if ("upcoming".equalsIgnoreCase(releasePhase)) {
                wrapper.gt(Film::getReleaseTime, today);
            }
        }

        // 排序：按上映时间倒序
        wrapper.orderByDesc(Film::getReleaseTime);

        // 分页查询
        Page<Film> pageParam = new Page<>(page, size);
        Page<Film> filmPage = filmMapper.selectPage(pageParam, wrapper);

        // 返回自定义分页结果
        return new PageResult<>(
                filmPage.getTotal(),
                (int) filmPage.getCurrent(),
                (int) filmPage.getSize(),
                filmPage.getRecords()
        );
    }

    // 根据地区和类型筛选电影
    @Override
    public List<Film> findByRegionAndType(String region, String type) {
        QueryWrapper<Film> wrapper = new QueryWrapper<>();
        if (!region.equals("全部")) {
            wrapper.in("region", region);
        }
        if (!type.equals("全部")) {
            wrapper.like("type", type);
        }
        return filmMapper.selectList(wrapper);
    }

    // 获取热门电影，按热度降序排列，返回指定数量
    @Override
    public List<Film> findHots(Integer limit) {
        QueryWrapper<Film> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("hot").last("limit " + limit);
        return filmMapper.selectList(wrapper);
    }

    // 根据电影名模糊搜索
    @Cacheable
    @Override
    public List<Film> findLikeName(String name) {
        QueryWrapper<Film> wrapper = new QueryWrapper<>();
        wrapper.like("name", name);
        return filmMapper.selectList(wrapper);
    }

    // 根据ID查找电影
    @Cacheable
    @Override
    public Film findById(String id) {
        return filmMapper.selectById(id);
    }

    // 更新电影信息
    @CacheEvict
    @Override
    public Film update(Film film) {
        normalizeFilmFields(film);
        filmMapper.updateById(film);
        return film;
    }

    private void normalizeFilmFields(Film film) {
        if (film == null) {
            return;
        }
        if (StringUtils.hasText(film.getReleaseTime())) {
            String releaseTime = FilmReleaseUtil.normalizeReleaseTime(film.getReleaseTime());
            if (releaseTime != null) {
                film.setReleaseTime(releaseTime);
            }
        }
        if (StringUtils.hasText(film.getType())) {
            String normalizedType = FilmTypeUtil.normalizeType(film.getType());
            if (normalizedType != null) {
                film.setType(normalizedType);
            }
        }
    }

}