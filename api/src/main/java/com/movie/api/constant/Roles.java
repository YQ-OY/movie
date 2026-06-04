package com.movie.api.constant;

/**
 * 权限常量（Spring Security 需带 "ROLE_" 前缀）
 * <p>
 * 员工账号：{@link #ROLE_WORKER} 为<strong>默认基础权限</strong>——新建员工时自动写入 {@code t_role}，
 * 且不可从后台删除；员工登录时 JWT 的 roles 中<strong>始终包含</strong> {@code ROLE_WORKER}（与库中记录无关也会注入）。
 * </p>
 * <p>
 * 下方「员工扩展」三类权限由管理员在后台勾选添加，写入 {@code t_role.value}，与 {@link #roles} 下拉一致。
 * </p>
 */
public final class Roles {

    public final static String ROLE_ADMIN = "ROLE_ADMIN";

    public final static String ROLE_USER = "ROLE_USER";

    /** 员工基础角色：每日工作、个人设置等；新建员工时自动入库，不可删除 */
    public final static String ROLE_WORKER = "ROLE_WORKER";

    // —— 员工扩展权限（管理员在后台为员工分配，写入 t_role；不含 ROLE_WORKER）——

    /**
     * 影视管理：电影增删改查、院线排片、轮播海报（原 ROLE_SCHEDULE_MANAGE 已并入本角色）
     */
    public final static String ROLE_FILM_MANAGE = "ROLE_FILM_MANAGE";

    /** 用户查看：查看用户列表 */
    public final static String ROLE_USER_VIEW = "ROLE_USER_VIEW";

    /** 订单管理：订单与异常订单管理 */
    public final static String ROLE_ORDER_MANAGE = "ROLE_ORDER_MANAGE";

    /**
     * 管理员为员工「添加权限」时的可选扩展项（不含 ROLE_WORKER，基础权限已默认具备）
     */
    public final static String[] roles = {
            ROLE_FILM_MANAGE,
            ROLE_USER_VIEW,
            ROLE_ORDER_MANAGE,
    };

}
