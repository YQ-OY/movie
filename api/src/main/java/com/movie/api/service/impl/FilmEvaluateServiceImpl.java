package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.movie.api.mapper.FilmEvaluateMapper;
import com.movie.api.mapper.FilmMapper;
import com.movie.api.mapper.UserMapper;
import com.movie.api.model.entity.Film;
import com.movie.api.model.entity.FilmEvaluate;
import com.movie.api.model.vo.FilmEvaluateVO;
import com.movie.api.service.FilmEvaluateService;
import com.movie.api.utils.DataTimeUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "filmEvaluate")
public class FilmEvaluateServiceImpl implements FilmEvaluateService {

    @Resource
    private FilmEvaluateMapper filmEvaluateMapper;

    @Resource
    private FilmMapper filmMapper;

    @Resource
    private UserMapper userMapper;

    // 保存电影评价（同时增加电影热度）
    @Override
    public void save(FilmEvaluate filmEvaluate) throws Exception {
        QueryWrapper<FilmEvaluate> wrapper = new QueryWrapper<>();
        wrapper.in("fid", filmEvaluate.getFid());
        wrapper.in("uid", filmEvaluate.getUid());
        FilmEvaluate one = filmEvaluateMapper.selectOne(wrapper);
        if (one != null) {
            throw new Exception("感谢您的参与，但是您已评分请不要重复操作！");
        }
        filmEvaluate.setCreateAt(DataTimeUtil.getNowTimeString());
        filmEvaluateMapper.insert(filmEvaluate);
        //为电影添加热度
        Film film = filmMapper.selectById(filmEvaluate.getFid());
        film.setHot(film.getHot() + 1);
        filmMapper.updateById(film);
    }

    // 根据电影ID查询所有评价（含用户信息）
    @Override
    public List<FilmEvaluateVO> findAllByFilmId(String fid) {
        List<FilmEvaluateVO> result = new ArrayList<>();
        List<FilmEvaluate> filmEvaluates = filmEvaluateMapper.selectList(new QueryWrapper<FilmEvaluate>().in("fid", fid));
        for (FilmEvaluate f : filmEvaluates) {
            FilmEvaluateVO filmEvaluateVO = new FilmEvaluateVO();
            filmEvaluateVO.setUser(userMapper.selectById(f.getUid()));
            filmEvaluateVO.setFilmEvaluate(f);
            filmEvaluateVO.setId(f.getId());
            result.add(filmEvaluateVO);
        }
        return result;
    }

    // 删除电影的所有评价
    @Override
    public void deleteAllByFilmId(String fid) {
        filmEvaluateMapper.delete(new QueryWrapper<FilmEvaluate>().in("fid", fid));
    }

    // 根据ID删除评价
    @Override
    public void deleteById(String id) {
        filmEvaluateMapper.deleteById(id);
    }

    // 计算电影平均星级（四舍五入到最近的0.5）
    @Override
    public Double averageStarRoundedHalf(String fid) {
        if (fid == null || fid.trim().isEmpty()) {
            return null;
        }
        BigDecimal avg = filmEvaluateMapper.selectAvgStarByFid(fid.trim());
        if (avg == null) {
            return null;
        }
        double v = avg.doubleValue();
        if (Double.isNaN(v)) {
            return null;
        }
        return Math.round(v * 2.0) / 2.0;
    }

    // 批量查询所有电影的平均星级（fid → 平均星级）
    @Override
    public Map<String, Double> mapAvgStarsRoundedHalfByFid() {
        List<Map<String, Object>> rows = filmEvaluateMapper.selectAvgStarGroupedByFid();
        Map<String, Double> out = new LinkedHashMap<>();
        if (rows == null) {
            return out;
        }
        for (Map<String, Object> row : rows) {
            Object fidObj = row.get("fid");
            Object avgObj = row.get("avgStar");
            if (avgObj == null) {
                avgObj = row.get("avgstar");
            }
            if (fidObj == null || avgObj == null) {
                continue;
            }
            double raw = avgObj instanceof BigDecimal
                    ? ((BigDecimal) avgObj).doubleValue()
                    : ((Number) avgObj).doubleValue();
            if (Double.isNaN(raw)) {
                continue;
            }
            out.put(String.valueOf(fidObj), Math.round(raw * 2.0) / 2.0);
        }
        return out;
    }

}