package com.movie.api.controller;

import com.movie.api.model.entity.DailyWork;
import com.movie.api.service.DailyWorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Tag(name = "每日工作接口")
@RequestMapping("/api/daily")
public class DailyWorkController {

    @Resource
    private DailyWorkService dailyWorkService;

    @PostMapping("")
    @Operation(summary = "添加每日工作")
    public void create(@RequestBody DailyWork dailyWork){
        dailyWorkService.save(dailyWork);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除每日工作")
    public void delete(@PathVariable String id){
        dailyWorkService.deleteById(id);
    }

    @GetMapping("")
    @Operation(summary = "查询所有")
    public List<DailyWork> findAll(){
        return dailyWorkService.findAll();
    }

}
