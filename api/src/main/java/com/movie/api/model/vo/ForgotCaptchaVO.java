package com.movie.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotCaptchaVO {

    private String captchaKey;

    private String captchaSvg;
}
