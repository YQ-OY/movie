package com.movie.api.service;

import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.vo.ArrangementVO;
import com.movie.api.model.vo.PageResult;

import java.util.List;

public interface ArrangementService {

    void save(Arrangement arrangement);

    List<Arrangement> findAll();

    ArrangementVO findByFilmId(String fid);

    List<Integer> getSeatsHaveSelected(Long id);

    Arrangement findById(Long id);

    void deleteById(Long id);

    Arrangement Update(Arrangement arrangement);

    PageResult<Arrangement> findByPage(Integer page, Integer size, String name, String startDate, String endDate);
}
