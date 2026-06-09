package com.movie.api.service;

import com.movie.api.model.entity.DailyWork;
import com.movie.api.model.vo.PageResult;

import java.util.List;

public interface DailyWorkService {

    void save(DailyWork dailyWork);

    List<DailyWork> findAll();

    void deleteById(String id);

    PageResult<DailyWork> findByPage(Integer page, Integer size, String keyword);
}
