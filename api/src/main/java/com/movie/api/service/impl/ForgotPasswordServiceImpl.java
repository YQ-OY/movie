package com.movie.api.service.impl;

import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.vo.ForgotCaptchaVO;
import com.movie.api.model.vo.ForgotSendCodeVO;
import com.movie.api.service.ForgotPasswordService;
import com.movie.api.support.CaptchaStore;
import com.movie.api.support.ForgotCodeStore;
import com.movie.api.utils.CaptchaUtil;
import com.movie.api.utils.ValidationUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    @Resource
    private CaptchaStore captchaStore;

    @Resource
    private ForgotCodeStore forgotCodeStore;

    @Override
    public ForgotCaptchaVO createCaptcha() {
        String code = CaptchaUtil.generateCode();
        String key = captchaStore.put(code);
        return new ForgotCaptchaVO(key, CaptchaUtil.renderSvg(code));
    }

    @Override
    public ForgotSendCodeVO sendCode(String accountType, ForgotSendCodeDto dto, boolean accountExists) throws Exception {
        if (dto == null) {
            throw new Exception("请求参数不能为空");
        }
        if (!captchaStore.verifyAndRemove(dto.getCaptchaKey(), dto.getCaptchaInput())) {
            throw new Exception("图形验证码错误或已过期");
        }
        ValidationUtil.requireValidMobileCN(dto.getPhone(), "手机号");
        if (!accountExists) {
            throw new Exception("该手机号未绑定账号");
        }
        forgotCodeStore.put(accountType, dto.getPhone());
        return new ForgotSendCodeVO(
                CaptchaUtil.maskPhone(ValidationUtil.normalizePhone(dto.getPhone())),
                ForgotCodeStore.DEMO_SMS_CODE
        );
    }

    @Override
    public void validateResetForm(ForgotResetDto dto) throws Exception {
        if (dto == null) {
            throw new Exception("请求参数不能为空");
        }
        ValidationUtil.requireValidMobileCN(dto.getPhone(), "手机号");
        if (ValidationUtil.isBlank(dto.getSmsCode())) {
            throw new Exception("请输入短信验证码");
        }
        if (ValidationUtil.isBlank(dto.getNewPassword())) {
            throw new Exception("请输入新密码");
        }
        if (dto.getNewPassword().length() < 6) {
            throw new Exception("密码长度不能少于6位");
        }
    }

    @Override
    public boolean verifySmsCode(String accountType, String phone, String code) {
        if (ValidationUtil.isBlank(code)) {
            return false;
        }
        String trimmed = code.trim();
        if (forgotCodeStore.matches(accountType, phone, trimmed)) {
            return true;
        }
        // 演示环境：内存验证码丢失（如服务重启）时仍接受固定演示码
        return ForgotCodeStore.DEMO_SMS_CODE.equals(trimmed);
    }

    @Override
    public void clearSmsCode(String accountType, String phone) {
        forgotCodeStore.remove(accountType, phone);
    }
}
