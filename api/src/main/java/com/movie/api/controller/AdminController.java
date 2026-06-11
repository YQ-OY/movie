package com.movie.api.controller;

import com.movie.api.annotation.DisableBaseResponse;
import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Admin;
import com.movie.api.model.support.ResponseResult;
import com.movie.api.model.vo.ForgotCaptchaVO;
import com.movie.api.model.vo.ForgotSendCodeVO;
import com.movie.api.service.AdminService;
import com.movie.api.service.ForgotPasswordService;
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

    @Resource
    private ForgotPasswordService forgotPasswordService;

    // 管理员登陆
    @PostMapping("/login")
    @Operation(summary = "管理员登陆")
    public Map<String, String> login(@RequestBody LoginDto loginDto) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", adminService.login(loginDto));
        return map;
    }

    // 管理员 更新个人信息
    @PostMapping("/update")
    @Operation(summary = "管理员更新个人信息")
    public Admin update(@RequestBody Admin admin) throws Exception {
        return adminService.update(admin);
    }

    // 获取当前登录管理员信息
    @GetMapping("/current")
    @Operation(summary = "获取当前登录管理员信息")
    public Admin getCurrent(@RequestHeader("Authorization") String token) {
        return adminService.getCurrent(token);
    }

    @GetMapping("/forgot/captcha")
    @Operation(summary = "忘记密码-获取图形验证码")
    public ForgotCaptchaVO forgotCaptcha() {
        return forgotPasswordService.createCaptcha();
    }

    @PostMapping("/forgot/send-code")
    @Operation(summary = "忘记密码-发送短信验证码（演示）")
    @DisableBaseResponse
    public ResponseResult<ForgotSendCodeVO> forgotSendCode(@RequestBody ForgotSendCodeDto dto) throws Exception {
        ForgotSendCodeVO vo = adminService.sendForgotCode(dto);
        return new ResponseResult<>("验证码已发送（演示验证码：123456）", vo);
    }

    @PostMapping("/forgot/verify-sms")
    @Operation(summary = "忘记密码-校验短信验证码")
    @DisableBaseResponse
    public ResponseResult<Void> forgotVerifySms(@RequestBody ForgotResetDto dto) throws Exception {
        adminService.verifyForgotSms(dto);
        return new ResponseResult<>("验证成功", null);
    }

    @PostMapping("/forgot/reset")
    @Operation(summary = "忘记密码-重置密码")
    @DisableBaseResponse
    public ResponseResult<Void> forgotReset(@RequestBody ForgotResetDto dto) throws Exception {
        adminService.resetPasswordByPhone(dto);
        return new ResponseResult<>("密码重置成功，请登录", null);
    }

}
