package com.movie.api.controller;

import com.movie.api.model.dto.LoginDto;
import com.movie.api.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "管理员接口")
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "管理员登陆")
    public Map<String, String> login(@RequestBody LoginDto loginDto) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", adminService.login(loginDto));
        return map;
    }

}
