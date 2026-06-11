package com.movie.api.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ForgotResetDto {

    private String phone;

    @JsonAlias("code")
    private String smsCode;

    private String newPassword;
}
