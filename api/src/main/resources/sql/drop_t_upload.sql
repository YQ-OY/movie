-- 移除历史图片二进制存储表（项目已改用 MinIO，按需执行一次）
DROP TABLE IF EXISTS `t_upload`;
