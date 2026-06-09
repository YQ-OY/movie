package com.movie.api.controller;

import com.movie.api.model.entity.Arrangement;
import com.movie.api.model.vo.ArrangementVO;
import com.movie.api.model.vo.SeatStatusVO;
import com.movie.api.service.ArrangementService;
import com.movie.api.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "电影排片场次接口")
@RequestMapping("/api/arrangement")
public class ArrangementController {

    @Resource
    private ArrangementService arrangementService;

    @Resource
    private FilmService filmService;

    @PostMapping("")
    @Operation(summary = "新增电影场次")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void save(@RequestBody Arrangement arrangement) {
        arrangementService.save(arrangement);
    }

    @PutMapping("")
    @Operation(summary = "修改排片信息")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public Arrangement update(@RequestBody Arrangement arrangement) {
        return arrangementService.Update(arrangement);
    }

    @DeleteMapping("")
    @Operation(summary = "根据id删除排片")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void delete(@RequestParam Long id) {
        arrangementService.deleteById(id);
    }

    @GetMapping("")
    @Operation(summary = "列出电影排片")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public List<Arrangement> list() {
        return arrangementService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询拍片")
    public Map<String, Object> findById(@PathVariable Long id) {
        HashMap<String, Object> map = new HashMap<>();
        Arrangement arrangement = arrangementService.findById(id);
        map.put("film", filmService.findById(arrangement.getFid()));
        map.put("arrangement", arrangement);
        return map;
    }

    @GetMapping("/getSeats")
    @Operation(summary = "获取座位情况（已售/锁座）")
    public SeatStatusVO getSeats(Long id) {
        return arrangementService.getSeatStatus(id);
    }

    @GetMapping("/film/{fid}")
    @Operation(summary = "查询某个电影的所有拍片")
    public ArrangementVO findByFilmId(@PathVariable String fid) {
        return arrangementService.findByFilmId(fid);
    }

}
