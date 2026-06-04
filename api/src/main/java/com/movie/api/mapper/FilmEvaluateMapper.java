package com.movie.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.api.model.entity.FilmEvaluate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface FilmEvaluateMapper extends BaseMapper<FilmEvaluate> {

    @Select("SELECT AVG(star) FROM t_film_evaluate WHERE fid = #{fid}")
    BigDecimal selectAvgStarByFid(@Param("fid") String fid);

    @Select("SELECT fid AS fid, AVG(star) AS avgStar FROM t_film_evaluate GROUP BY fid")
    List<Map<String, Object>> selectAvgStarGroupedByFid();
}
