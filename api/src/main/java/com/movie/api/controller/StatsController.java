package com.movie.api.controller;

import com.movie.api.model.vo.DashboardStatsVO;
import com.movie.api.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Tag(name = "数据统计")
@RequestMapping("/api/stats")
public class StatsController {

    @Resource
    private StatsService statsService;

    @GetMapping("/dashboard")
    @Operation(summary = "运营概览（订单、票房、用户等聚合）")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ORDER_MANAGE')")
    public DashboardStatsVO dashboard() {
        return statsService.buildDashboard();
    }
}
