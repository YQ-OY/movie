package com.movie.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.api.model.entity.Film;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FilmMapper extends BaseMapper<Film> {

    /** 在排片表中有场次的电影（用于前台「正在热播」） */
    @Select("SELECT * FROM t_film f WHERE EXISTS (SELECT 1 FROM t_arrangement a WHERE a.fid = f.id)")
    List<Film> selectFilmsWithArrangement();
}
