package com.movie.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 留言页等场景展示的客服信息（不含账号、密码；含对外联系电话便于用户咨询）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerPublicVO implements Serializable {

    private String id;

    private String nickname;

    private String gender;

    /** 对外联系电话（可为空） */
    private String phone;

    private String avatar;
}
