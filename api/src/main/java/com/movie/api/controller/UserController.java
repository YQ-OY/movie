package com.movie.api.controller;

import com.movie.api.annotation.DisableBaseResponse;
import com.movie.api.constant.Roles;
import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.User;
import com.movie.api.model.support.ResponseResult;
import com.movie.api.model.vo.ForgotCaptchaVO;
import com.movie.api.model.vo.ForgotSendCodeVO;
import com.movie.api.model.vo.PageResult;
import com.movie.api.service.ForgotPasswordService;
import com.movie.api.service.UserService;
import com.movie.api.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "用户接口")
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Map<String, Object> login(@RequestBody LoginDto dto) throws Exception {
        User user = userService.login(dto);
        Map<String, Object> map = new HashMap<>();
        //是否选择记住我
        long exp = dto.isRemember() ? JwtTokenUtil.REMEMBER_EXPIRATION_TIME : JwtTokenUtil.EXPIRATION_TIME;
        List<String> roles = new ArrayList<>();
        roles.add(Roles.ROLE_USER);
        map.put("token", JwtTokenUtil.createToken(dto.getUsername(), roles, exp));
        map.put("user", user);
        return map;
    }

    @GetMapping("")
    @Operation(summary = "查找所有用户")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER_VIEW')")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询用户，支持按用户名/昵称模糊搜索")
    public PageResult<User> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return userService.findByPage(page, size, keyword);
    }


    @PutMapping("")
    @Operation(summary = "更新用户")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public User updateUser(@RequestBody User user, Authentication authentication) throws Exception {
        if (authentication == null) {
            throw new AccessDeniedException("未登录");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin) {
            User existing = userService.findById(user.getId());
            if (existing == null || !existing.getUsername().equals(authentication.getName())) {
                throw new AccessDeniedException("只能修改本人账号信息");
            }
        }
        return userService.update(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查找用户")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER_VIEW','ROLE_USER')")
    public User findById(@PathVariable String id) {
        return userService.findById(id);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public User save(@RequestBody User user) throws Exception {
        return userService.save(user);
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
        ForgotSendCodeVO vo = userService.sendForgotCode(dto);
        return new ResponseResult<>("验证码已发送（演示验证码：123456）", vo);
    }

    @PostMapping("/forgot/verify-sms")
    @Operation(summary = "忘记密码-校验短信验证码")
    @DisableBaseResponse
    public ResponseResult<Void> forgotVerifySms(@RequestBody ForgotResetDto dto) throws Exception {
        userService.verifyForgotSms(dto);
        return new ResponseResult<>("验证成功", null);
    }

    @PostMapping("/forgot/reset")
    @Operation(summary = "忘记密码-重置密码")
    @DisableBaseResponse
    public ResponseResult<Void> forgotReset(@RequestBody ForgotResetDto dto) throws Exception {
        userService.resetPasswordByPhone(dto);
        return new ResponseResult<>("密码重置成功，请登录", null);
    }

}
