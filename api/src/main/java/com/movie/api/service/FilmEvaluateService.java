package com.movie.api.service;

import com.movie.api.model.entity.FilmEvaluate;
import com.movie.api.model.vo.FilmEvaluateVO;

import java.util.List;
import java.util.Map;

public interface FilmEvaluateService {

    void save(FilmEvaluate filmEvaluate) throws Exception;

    List<FilmEvaluateVO> findAllByFilmId(String fid);

    void deleteAllByFilmId(String fid);

    void deleteById(String id);

    /**
     * 该片所有评价星级均值，四舍五入到最近的 0.5；无评价时为 null。
     */
    Double averageStarRoundedHalf(String fid);

    /** fid → 均值（0.5 步长），用于列表批量展示 */
    Map<String, Double> mapAvgStarsRoundedHalfByFid();

}
