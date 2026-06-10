package com.movie.api.controller;

import com.movie.api.model.entity.Film;
import com.movie.api.model.vo.PageResult;
import com.movie.api.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Tag(name = "电影接口")
@RequestMapping("/api/film")
public class FilmController {

    @Resource
    private FilmService filmService;

    @PostMapping("")
    @Operation(summary = "保存电影")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void save(@RequestBody Film film) {
        filmService.save(film);
    }

    @GetMapping("")
    @Operation(summary = "列出所有电影")
    public List<Film> list(String region, String type) {
        if (region != null && type != null) {
            return filmService.findByRegionAndType(region, type);
        }
        return filmService.findAll();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询电影（支持名称模糊、类型多选、地区多选、状态筛选、上映阶段）")
    public PageResult<Film> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(required = false) String name,                 // 电影名称模糊搜索
            @RequestParam(required = false) List<String> type,          // 类型多选
            @RequestParam(required = false) List<String> region,        // 地区多选
            @RequestParam(required = false) Boolean status,             // 上架状态
            @RequestParam(required = false) String releasePhase) {      // showing / upcoming
        return filmService.findByPage(page, size, name, type, region, status, releasePhase);
    }

    @GetMapping("/hot/{limit}")
    @Operation(summary = "获取热榜电影")
    public List<Film> listHots(@PathVariable Integer limit) {
        return filmService.findHots(limit);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "搜索电影")
    public List<Film> search(@PathVariable String name) {
        return filmService.findLikeName(name);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查找电影")
    public Film findById(@PathVariable String id) {
        return filmService.findById(id);
    }

    @PutMapping("")
    @Operation(summary = "更新电影")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void update(@RequestBody Film film) {
        filmService.update(film);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除电影")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void deleteById(@PathVariable String id) {
        filmService.deleteById(id);
    }

}
