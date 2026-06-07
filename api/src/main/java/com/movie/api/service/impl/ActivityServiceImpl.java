package com.movie.api.service.impl;

import com.movie.api.mapper.ActivityMapper;
import com.movie.api.model.entity.Activity;
import com.movie.api.service.ActivityService;
import com.movie.api.utils.DataTimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private ActivityMapper activityMapper;

    // 创建活动
    @Override
    public void create(Activity activity) {
        activity.setId(UUID.randomUUID().toString());
        activity.setCreateAt(DataTimeUtil.getNowTimeString());
        activityMapper.insert(activity);
    }

    // 根据ID查询活动
    @Override
    public Activity findById(String id) {
        return activityMapper.selectById(id);
    }

    // 查询所有活动
    @Override
    public List<Activity> findAll() {
        return activityMapper.selectList(null);
    }

    // 根据ID删除活动
    @Override
    public void deleteById(String id) {
        activityMapper.deleteById(id);
    }

}