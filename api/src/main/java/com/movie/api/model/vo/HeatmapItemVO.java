package com.movie.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 热力图数据项：x 轴、y 轴、数值
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapItemVO {

    private String name;

    private Integer value;
}
