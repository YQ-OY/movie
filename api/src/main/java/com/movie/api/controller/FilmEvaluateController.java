package com.movie.api.controller;

import com.movie.api.model.entity.FilmEvaluate;
import com.movie.api.model.vo.FilmEvaluateVO;
import com.movie.api.service.FilmEvaluateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "电影评价接口")
@RequestMapping("/api/fe")
public class FilmEvaluateController {

    @Resource
    private FilmEvaluateService filmEvaluateService;

    @PostMapping("")
    @Operation(summary = "评论电影")
    public void save(@RequestBody FilmEvaluate filmEvaluate) throws Exception {
        filmEvaluateService.save(filmEvaluate);
    }

    @GetMapping("")
    @Operation(summary = "获取电影评论")
    public List<FilmEvaluateVO> list(@RequestParam(name = "fid") String fid) {
        if (fid != null) {
            return filmEvaluateService.findAllByFilmId(fid);
        }
        return null;
    }

    @GetMapping("/avg")
    @Operation(summary = "该片评分均值（按 0.5 步长），无评价时为 null")
    public Double averageStar(@RequestParam(name = "fid") String fid) {
        return filmEvaluateService.averageStarRoundedHalf(fid);
    }

    @GetMapping("/avgs")
    @Operation(summary = "全部影片评分均值（按电影分组，0.5 步长），用于列表封面展示")
    public Map<String, Double> allAverageStars() {
        return filmEvaluateService.mapAvgStarsRoundedHalfByFid();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除评论")
    public void remove(@PathVariable String id) {
        filmEvaluateService.deleteById(id);
    }

    @DeleteMapping("")
    @Operation(summary = "删除该电影的所有评分")
    public void removeAll(@RequestParam(name = "fid") String fid) {
        filmEvaluateService.deleteAllByFilmId(fid);
    }

}
