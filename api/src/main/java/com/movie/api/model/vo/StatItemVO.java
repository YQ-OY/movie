package com.movie.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图表通用一项：名称 + 数值
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatItemVO {

    private String name;

    private Number value;
}
