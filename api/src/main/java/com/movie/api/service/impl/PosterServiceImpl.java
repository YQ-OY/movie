package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.movie.api.mapper.PosterMapper;
import com.movie.api.model.entity.Poster;
import com.movie.api.service.PosterService;
import com.movie.api.utils.DataTimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class PosterServiceImpl implements PosterService {

    @Resource
    private PosterMapper posterMapper;

    // 保存海报
    @Override
    public void save(Poster poster) {
        poster.setId(UUID.randomUUID().toString());
        poster.setCreateAt(DataTimeUtil.getNowTimeString());
        posterMapper.insert(poster);
    }

    // 更新海报
    @Override
    public void update(Poster poster) {
        posterMapper.updateById(poster);
    }

    // 查询所有海报
    @Override
    public List<Poster> findAll() {
        return posterMapper.selectList(null);
    }

    // 根据状态查询海报
    @Override
    public List<Poster> findByStatus(boolean status) {
        QueryWrapper<Poster> wrapper = new QueryWrapper<>();
        wrapper.in("status", status);
        return posterMapper.selectList(wrapper);
    }

    // 根据ID删除海报
    @Override
    public void deleteById(String id) {
        posterMapper.deleteById(id);
    }

    // 删除所有海报
    @Override
    public void deleteAll() {
        posterMapper.delete(null);
    }

}