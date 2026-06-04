package com.movie.api.controller;

import com.movie.api.model.entity.Registration;
import com.movie.api.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Tag(name = "活动接口")
@RequestMapping("/api/registration")
public class RegistrationController {

    @Resource
    private RegistrationService registrationService;

    @PostMapping("")
    @Operation(summary = "报名活动")
    public void create(@RequestBody Registration registration) throws Exception {
        registrationService.create(registration);
    }

    @GetMapping("")
    @Operation(summary = "查询所有报名信息")
    public List<Registration> create() {
        return registrationService.findAll();
    }

}
