package com.movie.api.service;

import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.vo.ForgotCaptchaVO;
import com.movie.api.model.vo.ForgotSendCodeVO;

public interface ForgotPasswordService {

    ForgotCaptchaVO createCaptcha();

    ForgotSendCodeVO sendCode(String accountType, ForgotSendCodeDto dto, boolean accountExists) throws Exception;

    void validateResetForm(ForgotResetDto dto) throws Exception;

    boolean verifySmsCode(String accountType, String phone, String code);

    void clearSmsCode(String accountType, String phone);
}
