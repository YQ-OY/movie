-- 可选：将存量「YYYY年MM月DD日」格式统一为 YYYY-MM-DD
-- 执行前请备份 t_film 表

UPDATE t_film
SET release_time = CONCAT(
    SUBSTRING_INDEX(release_time, '年', 1), '-',
    LPAD(SUBSTRING_INDEX(SUBSTRING_INDEX(release_time, '年', -1), '月', 1), 2, '0'), '-',
    LPAD(SUBSTRING_INDEX(SUBSTRING_INDEX(release_time, '月', -1), '日', 1), 2, '0')
)
WHERE release_time LIKE '%年%月%日'
  AND release_time NOT LIKE '%-%';
