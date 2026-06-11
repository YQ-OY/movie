package com.movie.api.model.dto;

import lombok.Data;

@Data
public class ForgotSendCodeDto {

    private String phone;

    private String captchaKey;

    private String captchaInput;
}
