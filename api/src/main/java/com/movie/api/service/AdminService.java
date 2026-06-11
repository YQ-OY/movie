package com.movie.api.service;

import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Admin;
import com.movie.api.model.vo.ForgotSendCodeVO;

public interface AdminService {

    String login(LoginDto loginDto) throws Exception;

    Admin update(Admin admin);

    Admin getCurrent(String token);

    Admin findByPhone(String phone);

    ForgotSendCodeVO sendForgotCode(ForgotSendCodeDto dto) throws Exception;

    void verifyForgotSms(ForgotResetDto dto) throws Exception;

    void resetPasswordByPhone(ForgotResetDto dto) throws Exception;
}
