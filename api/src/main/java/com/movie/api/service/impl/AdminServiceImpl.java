package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.movie.api.constant.Roles;
import com.movie.api.mapper.AdminMapper;
import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Admin;
import com.movie.api.model.vo.ForgotSendCodeVO;
import com.movie.api.service.AdminService;
import com.movie.api.service.ForgotPasswordService;
import com.movie.api.utils.JwtTokenUtil;
import com.movie.api.utils.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private ForgotPasswordService forgotPasswordService;

    // 管理员登录验证，返回JWT令牌
    @Override
    public String login(LoginDto loginDto) throws Exception {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.in("username", loginDto.getUsername());
        wrapper.in("password", loginDto.getPassword());
        Admin admin = adminMapper.selectOne(wrapper);
        if (admin == null) throw new Exception("用户名密码错误");
        //是否选择记住我
        long exp = loginDto.isRemember() ? JwtTokenUtil.REMEMBER_EXPIRATION_TIME : JwtTokenUtil.EXPIRATION_TIME;
        List<String> roles = new ArrayList<>();
        roles.add(Roles.ROLE_ADMIN);
        return JwtTokenUtil.createToken(loginDto.getUsername(), roles, exp);
    }

    // 更新管理员信息
    @Override
    public Admin update(Admin admin) {
        adminMapper.updateById(admin);
        return admin;
    }

    // 获取当前登录管理员信息
    @Override
    public Admin getCurrent(String token) {
        String jwt = token.replace("Bearer ", "");
        String username = JwtTokenUtil.getUsername(jwt);
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return adminMapper.selectOne(wrapper);
    }

    @Override
    public Admin findByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getPhone, phone.trim());
        return adminMapper.selectOne(wrapper);
    }

    @Override
    public ForgotSendCodeVO sendForgotCode(ForgotSendCodeDto dto) throws Exception {
        Admin admin = findByPhone(dto.getPhone());
        return forgotPasswordService.sendCode("admin", dto, admin != null);
    }

    @Override
    public void verifyForgotSms(ForgotResetDto dto) throws Exception {
        ValidationUtil.requireValidMobileCN(dto.getPhone(), "手机号");
        if (ValidationUtil.isBlank(dto.getSmsCode())) {
            throw new Exception("请输入短信验证码");
        }
        Admin admin = findByPhone(dto.getPhone());
        if (admin == null) {
            throw new Exception("该手机号未绑定账号");
        }
        if (!forgotPasswordService.verifySmsCode("admin", dto.getPhone(), dto.getSmsCode())) {
            throw new Exception("短信验证码错误或已过期");
        }
    }

    @Override
    public void resetPasswordByPhone(ForgotResetDto dto) throws Exception {
        forgotPasswordService.validateResetForm(dto);
        Admin admin = findByPhone(dto.getPhone());
        if (admin == null) {
            throw new Exception("该手机号未绑定账号");
        }
        if (!forgotPasswordService.verifySmsCode("admin", dto.getPhone(), dto.getSmsCode())) {
            throw new Exception("短信验证码错误或已过期");
        }
        admin.setPassword(dto.getNewPassword());
        adminMapper.updateById(admin);
        forgotPasswordService.clearSmsCode("admin", dto.getPhone());
    }
}