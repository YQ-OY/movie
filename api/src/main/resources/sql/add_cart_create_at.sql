-- 购物车锁座：记录加入购物车时间，用于 10 分钟锁座
ALTER TABLE t_cart ADD COLUMN create_at VARCHAR(32) NULL COMMENT '加入购物车时间';
