package com.movie.api.service.impl;

import com.movie.api.mapper.DailyWorkMapper;
import com.movie.api.model.entity.DailyWork;
import com.movie.api.service.DailyWorkService;
import com.movie.api.utils.DataTimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class DailyWorkServiceImpl implements DailyWorkService {

    @Resource
    private DailyWorkMapper dailyWorkMapper;

    // 保存工作日志
    @Override
    public void save(DailyWork dailyWork) {
        dailyWork.setId(UUID.randomUUID().toString());
        dailyWork.setCreateAt(DataTimeUtil.getNowTimeString());
        dailyWorkMapper.insert(dailyWork);
    }

    // 查询所有工作日志
    @Override
    public List<DailyWork> findAll() {
        return dailyWorkMapper.selectList(null);
    }

    // 根据ID删除工作日志
    @Override
    public void deleteById(String id) {
        dailyWorkMapper.deleteById(id);
    }

}