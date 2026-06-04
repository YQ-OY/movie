package com.movie.api.controller;

import com.movie.api.model.entity.Activity;
import com.movie.api.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Tag(name = "活动接口")
@RequestMapping("/api/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @PostMapping("")
    @Operation(summary = "新增活动")
    public void create(@RequestBody Activity activity) {
        activityService.create(activity);
    }

    @GetMapping("")
    @Operation(summary = "获取全部活动")
    public List<Activity> findAll() {
        return activityService.findAll();
    }

    @GetMapping("{id}")
    @Operation(summary = "根据id查找活动")
    public Activity findById(@PathVariable String id) {
        return activityService.findById(id);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除活动")
    public void delete(@PathVariable String id) {
        activityService.deleteById(id);
    }

}
