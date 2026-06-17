/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80404 (8.4.4)
 Source Host           : localhost:3306
 Source Schema         : movie

 Target Server Type    : MySQL
 Target Server Version : 80404 (8.4.4)
 File Encoding         : 65001

 Date: 17/06/2026 14:42:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_activity`;
CREATE TABLE `t_activity`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `number` int NULL DEFAULT 0,
  `start_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `end_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_activity
-- ----------------------------
INSERT INTO `t_activity` VALUES ('4d0667de-c19b-450f-b3dd-e43322c6dbda', '五一狂欢', 1, '2026-05-03', '2026-05-08', '2026-05-03 12:31:43');
INSERT INTO `t_activity` VALUES ('584aebcb-9f52-4e20-b8f4-969503e08a85', '活动简述:为了满足广大儿童与家长的娱乐、交流、学习需求,让我们的孩子们有-块专属于自己的屏幕与天空，晚报拟将晚报内部电影厅打造成专属的儿童亲子影院，定期播放专属儿童的各类电影与演出活动,如英语专场、爸爸去看电影专场、假期电影周、动漫专场等等,让亲子家庭在娱乐中提升、在快乐中沉思。同时影院将并与商业机构合作,开展各类知识讲座与交流,如儿童保险、家庭理财、儿童教育、母婴用品交流等等，在丰富儿童的业余生活，服务亲子家庭的基本需求的同时，促进家庭和谐，让父母与子女相互尊重、共同教育、-起成长。\n', 0, '2021-04-01', '2021-04-03', '2021-03-31 17:04:58');
INSERT INTO `t_activity` VALUES ('c0932fdf-c8b2-4ef3-a186-e80287364c0a', '高考\n', 1, '2026-06-10', '2026-06-12', '2026-06-09 22:48:46');

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'Admin',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `birthday` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `gender` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `info` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '青山不识我姓字，我亦不识青山名。',
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_admin
-- ----------------------------
INSERT INTO `t_admin` VALUES ('1', '@admin', '123456', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'Admin', '13655556666', '2000-06-01', '男', '青山不识我姓字，我亦不识青山名。', '1183136757@qq.com');

-- ----------------------------
-- Table structure for t_arrangement
-- ----------------------------
DROP TABLE IF EXISTS `t_arrangement`;
CREATE TABLE `t_arrangement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seat_number` int NOT NULL,
  `box_office` int NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `start_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `end_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `founder` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_arrangement_fid`(`fid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_arrangement
-- ----------------------------
INSERT INTO `t_arrangement` VALUES (13, '2fbca18717fa1039c5ebaf40e216a3f5', '情书', 80, 1830, 30.50, '2D放映', '2026-06-14', '19:30:00', '21:27:00', 'admin', '2026-06-10 09:00:00');
INSERT INTO `t_arrangement` VALUES (14, '2fbca18717fa1039c5ebaf40e216a3f5', '情书', 80, 0, 30.50, '2D放映', '2026-06-18', '18:00:00', '19:57:00', 'admin', '2026-06-17 13:04:24');
INSERT INTO `t_arrangement` VALUES (15, '2fbca18717fa1039c5ebaf40e216a3f5', '情书', 80, 0, 30.50, '2D放映', '2026-06-19', '18:00:00', '19:57:00', 'admin', '2026-06-17 13:05:19');
INSERT INTO `t_arrangement` VALUES (16, '2fbca18717fa1039c5ebaf40e216a3f5', '情书', 80, 0, 35.00, '2D放映', '2026-06-21', '14:30:00', '16:27:00', 'admin', '2026-06-17 14:00:00');
INSERT INTO `t_arrangement` VALUES (17, 'e6db3812245e74ca0c397ae58cd9bf54', '西线无战事 Im Westen nichts Neues ', 60, 2560, 32.00, '2D放映', '2026-06-07', '15:30:00', '18:08:00', 'admin', '2026-06-05 10:00:00');
INSERT INTO `t_arrangement` VALUES (18, 'e6db3812245e74ca0c397ae58cd9bf54', '西线无战事 Im Westen nichts Neues ', 60, 1920, 32.00, '2D放映', '2026-06-08', '19:00:00', '21:38:00', 'admin', '2026-06-05 10:05:00');
INSERT INTO `t_arrangement` VALUES (19, 'e6db3812245e74ca0c397ae58cd9bf54', '西线无战事 Im Westen nichts Neues ', 40, 0, 32.00, '2D放映', '2026-06-20', '15:45:00', '18:23:00', 'admin', '2026-06-17 13:43:09');
INSERT INTO `t_arrangement` VALUES (20, '565c5c36cd3a208740e9e21b7f0a6937', '火遮眼', 80, 3150, 38.00, '2D放映', '2026-06-13', '20:00:00', '21:53:00', 'admin', '2026-06-11 08:00:00');
INSERT INTO `t_arrangement` VALUES (21, '565c5c36cd3a208740e9e21b7f0a6937', '火遮眼', 80, 2660, 38.00, '2D放映', '2026-06-15', '19:30:00', '21:23:00', 'admin', '2026-06-11 08:10:00');
INSERT INTO `t_arrangement` VALUES (22, '565c5c36cd3a208740e9e21b7f0a6937', '火遮眼', 80, 0, 38.00, '2D放映', '2026-06-17', '21:00:00', '22:53:00', 'admin', '2026-06-17 09:00:00');
INSERT INTO `t_arrangement` VALUES (23, '565c5c36cd3a208740e9e21b7f0a6937', '火遮眼', 80, 0, 42.00, '3D放映', '2026-06-18', '19:45:00', '21:38:00', 'admin', '2026-06-17 09:05:00');
INSERT INTO `t_arrangement` VALUES (24, '565c5c36cd3a208740e9e21b7f0a6937', '火遮眼', 80, 0, 38.00, '2D放映', '2026-06-20', '14:00:00', '15:53:00', 'admin', '2026-06-17 09:10:00');
INSERT INTO `t_arrangement` VALUES (25, 'a7391218ec253fbcea19311da738dc4e', '揭秘日', 80, 2280, 35.00, '2D放映', '2026-06-14', '16:00:00', '18:36:00', 'admin', '2026-06-12 08:00:00');
INSERT INTO `t_arrangement` VALUES (26, 'a7391218ec253fbcea19311da738dc4e', '揭秘日', 80, 0, 35.00, '2D放映', '2026-06-17', '19:30:00', '22:06:00', 'admin', '2026-06-17 09:20:00');
INSERT INTO `t_arrangement` VALUES (27, 'a7391218ec253fbcea19311da738dc4e', '揭秘日', 80, 0, 40.00, '3D放映', '2026-06-19', '20:15:00', '22:51:00', 'admin', '2026-06-17 09:25:00');
INSERT INTO `t_arrangement` VALUES (28, '15e9ffc16b8869e0a03030aae2d0e761', '记忆碎片', 60, 1460, 32.00, '2D放映', '2026-06-08', '14:00:00', '16:04:00', 'admin', '2026-06-01 10:00:00');
INSERT INTO `t_arrangement` VALUES (29, '15e9ffc16b8869e0a03030aae2d0e761', '记忆碎片', 60, 0, 32.00, '2D放映', '2026-06-18', '13:20:00', '15:24:00', 'admin', '2026-06-17 10:00:00');
INSERT INTO `t_arrangement` VALUES (30, '2615b806585489d907c35df63ad888a9', '星球大战：曼达洛人与古古', 80, 3780, 42.00, '3D放映', '2026-06-07', '19:00:00', '21:23:00', 'admin', '2026-05-25 10:00:00');
INSERT INTO `t_arrangement` VALUES (31, '2615b806585489d907c35df63ad888a9', '星球大战：曼达洛人与古古', 80, 0, 42.00, '3D放映', '2026-06-17', '14:30:00', '16:53:00', 'admin', '2026-06-17 10:10:00');
INSERT INTO `t_arrangement` VALUES (32, '2615b806585489d907c35df63ad888a9', '星球大战：曼达洛人与古古', 80, 0, 42.00, '3D放映', '2026-06-21', '19:00:00', '21:23:00', 'admin', '2026-06-17 10:15:00');
INSERT INTO `t_arrangement` VALUES (33, '1606c3b18477393d675696ffe41d51c8', '家弑服务', 60, 1280, 33.00, '2D放映', '2026-06-10', '21:00:00', '23:21:00', 'admin', '2026-06-01 11:00:00');
INSERT INTO `t_arrangement` VALUES (34, '1606c3b18477393d675696ffe41d51c8', '家弑服务', 60, 0, 33.00, '2D放映', '2026-06-19', '22:00:00', '00:21:00', 'admin', '2026-06-17 10:20:00');
INSERT INTO `t_arrangement` VALUES (35, '2d7441d137ca0c1ce7c00fb89ae77490', '诺曼底72小时', 60, 960, 30.00, '2D放映', '2026-06-08', '10:30:00', '12:20:00', 'admin', '2026-06-06 09:00:00');
INSERT INTO `t_arrangement` VALUES (36, '2d7441d137ca0c1ce7c00fb89ae77490', '诺曼底72小时', 60, 0, 30.00, '2D放映', '2026-06-18', '16:10:00', '18:00:00', 'admin', '2026-06-17 10:30:00');
INSERT INTO `t_arrangement` VALUES (37, 'ddc3a73a828a5d653cc4a88636ba62e0', '消失的人', 80, 4200, 36.00, '2D放映', '2026-05-03', '19:00:00', '21:30:00', 'admin', '2026-04-28 09:00:00');
INSERT INTO `t_arrangement` VALUES (38, 'ddc3a73a828a5d653cc4a88636ba62e0', '消失的人', 80, 0, 36.00, '2D放映', '2026-06-17', '13:00:00', '15:30:00', 'admin', '2026-06-17 08:00:00');
INSERT INTO `t_arrangement` VALUES (39, '33b0ba5f267c283115bf14092869278b', ' 只此青绿', 80, 1780, 32.00, '2D放映', '2026-06-01', '14:00:00', '15:39:00', 'admin', '2026-05-30 09:00:00');
INSERT INTO `t_arrangement` VALUES (40, '33b0ba5f267c283115bf14092869278b', ' 只此青绿', 80, 0, 32.00, '2D放映', '2026-06-20', '10:30:00', '12:09:00', 'admin', '2026-06-17 11:00:00');
INSERT INTO `t_arrangement` VALUES (41, 'a33f89d02bdbfc832170cfa8e1885b33', '森中有林 ', 60, 990, 31.00, '2D放映', '2026-06-05', '19:30:00', '21:37:00', 'admin', '2026-05-25 10:00:00');
INSERT INTO `t_arrangement` VALUES (42, '1ef0569f05ff6cbdcbcd59c133453ebb', ' 爱是愤怒', 80, 0, 35.00, '2D放映', '2026-06-19', '10:00:00', '11:43:00', 'admin', '2026-06-15 09:00:00');
INSERT INTO `t_arrangement` VALUES (43, '1ef0569f05ff6cbdcbcd59c133453ebb', ' 爱是愤怒', 80, 0, 35.00, '2D放映', '2026-06-19', '19:30:00', '21:13:00', 'admin', '2026-06-15 09:05:00');
INSERT INTO `t_arrangement` VALUES (44, 'a6cc9fe1808ad045295d29c9c353ddc0', '玩具总动员5 （Toy Story 5）', 80, 0, 38.00, '2D放映', '2026-06-19', '11:00:00', '12:52:00', 'admin', '2026-06-15 09:10:00');
INSERT INTO `t_arrangement` VALUES (45, 'a6cc9fe1808ad045295d29c9c353ddc0', '玩具总动员5 （Toy Story 5）', 80, 0, 40.00, '3D放映', '2026-06-19', '15:30:00', '17:22:00', 'admin', '2026-06-15 09:15:00');
INSERT INTO `t_arrangement` VALUES (46, '3aee3a3a443812797762520899873916', '抓特务', 80, 0, 36.00, '2D放映', '2026-06-19', '13:30:00', '16:01:00', 'admin', '2026-06-15 09:20:00');
INSERT INTO `t_arrangement` VALUES (47, 'ec8b7b600d6c2639d4449632352d72ad', '阴风诡影', 60, 0, 33.00, '2D放映', '2026-06-19', '22:30:00', '00:08:00', 'admin', '2026-06-15 09:25:00');
INSERT INTO `t_arrangement` VALUES (48, '38843d2b9e2dcb308168ff08ee32306d', '我看见两朵一样的云', 60, 0, 34.00, '2D放映', '2026-06-19', '17:00:00', '18:54:00', 'admin', '2026-06-15 09:30:00');
INSERT INTO `t_arrangement` VALUES (49, '65faffd2dec3b7ef941d56566aa4be30', '四渡', 80, 0, 35.00, '2D放映', '2026-06-26', '10:00:00', '12:12:00', 'admin', '2026-06-17 11:30:00');
INSERT INTO `t_arrangement` VALUES (50, '75b846036c76335c34d14ef71fed781f', '超级少女', 80, 0, 40.00, '3D放映', '2026-06-26', '19:00:00', '21:00:00', 'admin', '2026-06-17 11:35:00');
INSERT INTO `t_arrangement` VALUES (51, 'a28c2f58aa9d33c83719146306ec9ac7', '小气鬼', 80, 0, 32.00, '2D放映', '2026-06-27', '14:00:00', '15:53:00', 'admin', '2026-06-17 11:40:00');

-- ----------------------------
-- Table structure for t_cart
-- ----------------------------
DROP TABLE IF EXISTS `t_cart`;
CREATE TABLE `t_cart`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `uid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `aid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` int NULL DEFAULT NULL,
  `seats` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `create_at` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '加入购物车时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_cart
-- ----------------------------
INSERT INTO `t_cart` VALUES ('c101a001-0001-4000-8000-000000000001', '1db44f28-c2ac-470b-a889-56ed14ab7202', '23', '15537739989', NULL, '6号7号', 84.00, '2026-06-17 15:48:00');
INSERT INTO `t_cart` VALUES ('c101a002-0002-4000-8000-000000000002', 'b8cd70cd-4c8e-4ed2-a365-94740053807b', '45', '18678945632', NULL, '12号', 40.00, '2026-06-17 15:50:30');
INSERT INTO `t_cart` VALUES ('c101a003-0003-4000-8000-000000000003', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '26', '13639677035', NULL, '3号', 35.00, '2026-06-17 15:51:10');

-- ----------------------------
-- Table structure for t_daily_work
-- ----------------------------
DROP TABLE IF EXISTS `t_daily_work`;
CREATE TABLE `t_daily_work`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `type` int NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_daily_work
-- ----------------------------
INSERT INTO `t_daily_work` VALUES ('cebee2f6-5f49-4d10-9af0-05445c842188', 1, '进行用户满意度调查', '2026-06-17 12:17:18');

-- ----------------------------
-- Table structure for t_film
-- ----------------------------
DROP TABLE IF EXISTS `t_film`;
CREATE TABLE `t_film`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `release_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` tinyint NOT NULL,
  `region` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `hot` int NOT NULL,
  `introduction` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `cover` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `duration` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_film
-- ----------------------------
INSERT INTO `t_film` VALUES ('15e9ffc16b8869e0a03030aae2d0e761', '记忆碎片', '2026-05-29', '悬疑,犯罪,惊悚', 1, '美国', 0, '诺兰神作大银幕巨献，2026烧脑悬疑佳作，百万观众口碑认证，豆瓣8.7高分！莱纳德在家中遭到歹徒袭击，妻子遇害，脑部重创患上短期失忆症，仅能保留十分钟记忆。为替亡妻复仇，他依靠纹身、纸条、照片拼凑线索追查真凶。随着调查层层回溯，身边之人皆身份成谜。记忆的真伪、复仇的意义与自我的认知重重交织，在这场迷雾中，他究竟该相信谁？谁才是幕后真凶？', 'http://127.0.0.1:8888/api/upload/view?key=2fcc40ac-edc6-43e1-8f4f-4fcb9ad8f275.jpg', 114);
INSERT INTO `t_film` VALUES ('1606c3b18477393d675696ffe41d51c8', '家弑服务', '2026-05-29', '惊悚,悬疑', 1, '英国', 0, '尺度生猛刀刀见血，悬疑惊悚爆款爽片！保姆阔太恶女联手，惩戒清算豪门渣男，手起刀落贴脸开杀！高口碑，大银幕，看得上头，爽到飞起！ 走投无路的米莉（西德尼·斯维尼 饰）侥幸踏入顶级豪门做起住家女佣，本以为是绝境中求得生路，却不料豪宅中封闭的阁楼、蹊跷的药物以及夜晚的惊叫，处处透露着诡异。喜怒无常的女主人妮娜（阿曼达·塞弗里德 饰）频频发难，而男主人安德鲁（布兰登·斯克莱纳 饰）则温柔示好恰到拿捏分寸……光鲜豪门，人心难测，暗藏惊天秘闻。在欲望与伪装交错的华丽深宅里，究竟谁是暗中蛰伏的猎人，谁又是身不由己的猎物？', 'http://127.0.0.1:8888/api/upload/view?key=f7f1146b-2d4f-4190-b6cc-30477e1ba962.jpg', 131);
INSERT INTO `t_film` VALUES ('1ef0569f05ff6cbdcbcd59c133453ebb', ' 爱是愤怒', '2026-06-19', '爱情,犯罪', 1, '中国大陆', 0, '如果我想和你有个家，你会不会觉得我幼稚？刚步入社会的小镇青年刘浩（王安宇 饰），无论在工作和生活中，始终都是遭人轻视的那个。直到他遇见菜菜（王玉雯 饰），两位年轻人陷入了爱河。然而，就在他们满怀信心为自己的小家打拼之际，一场意外将他们的生活彻底改变。面对突然向他们砸来的狂风暴雨，在那个“为你遮风挡雨”的承诺面前，他们是否能守护好共同的家……', 'http://127.0.0.1:8888/api/upload/view?key=64ed7472-f04a-4c15-a543-b3ac7747f73d.jpg', 93);
INSERT INTO `t_film` VALUES ('2615b806585489d907c35df63ad888a9', '星球大战：曼达洛人与古古', '2026-05-22', '科幻,冒险,动作', 1, '美国', 0, '在由卢卡斯影业打造的星球大战系列全新冒险电影《星球大战：曼达洛人与古古》（Star Wars: The Mandalorian and Grogu）中，曼达洛人与古古将踏上他们迄今为止最惊心动魄的任务。本片将于2026年5月22日正式登陆内地各大院线。此时，邪恶的帝国已经土崩瓦解，但残余的帝国军阀仍散布在银河系的各个角落。当羽翼未丰的新共和国正竭力守护义军同盟曾为之奋斗的一切时，他们招募了传奇曼达洛赏金猎人丁·贾林（佩德罗·帕斯卡 饰）及其年轻的学徒古古前来助阵。《星球大战：曼达洛人与古古》由乔·费儒执导，西格妮·韦弗加盟出演。本片由乔·费儒、凯瑟琳·肯尼迪、戴夫·费罗尼及伊恩·布莱斯共同担任制片人，并由路德维格·戈兰松担纲配乐。', 'http://127.0.0.1:8888/api/upload/view?key=0f7282e5-80e1-45da-9c9b-4093b5da0882.jpg', 133);
INSERT INTO `t_film` VALUES ('2d7441d137ca0c1ce7c00fb89ae77490', '诺曼底72小时', '2026-06-06', '战争', 1, '法国', 0, '故事聚焦诺曼底登陆前夕的紧张局势，以盟军远征军最高司令部首席气象学家詹姆斯·斯塔格上尉为主线。他肩负着一项关键职责，即向盟军最高指挥官德怀特·戴维·艾森豪威尔汇报那些决定诺曼底登陆成败的天气状况。', 'http://127.0.0.1:8888/api/upload/view?key=0bf210e9-a082-4f1f-8458-63a1d4e1f454.jpg', 100);
INSERT INTO `t_film` VALUES ('2fbca18717fa1039c5ebaf40e216a3f5', '情书', '2021-05-20', '爱情', 1, '日本', 2, '       日本神户某个飘雪的冬日，渡边博子（中山美穗）在前未婚夫藤井树的两周年祭日上又一次悲痛到不能自已。正因为无法抑制住对已逝恋人的思念，渡边博子在其中学同学录里发现“藤井树” 在小樽市读书时的地址时，依循着寄发了一封本以为是发往天国的情书。\n　　不想不久渡边博子竟然收到署名为“藤井树（酒井美纪）”的回信，经过进一步了解，她知晓此藤井树是一个同她年纪相仿的女孩，且还是男友藤井树（柏原崇）少年时代的同班同学。为了多了解一些昔日恋人在中学时代的情况，渡边博子开始与女性藤井树书信往来。而藤井树在不断的回忆中，渐渐发现少年时代与她同名同姓的那个藤井树曾对自己藏了一腔柔情。', 'http://127.0.0.1:8888/api/upload/view?key=d9ff72e7-b2e9-4036-8b5a-4de98dae22b9.jpg', 117);
INSERT INTO `t_film` VALUES ('33b0ba5f267c283115bf14092869278b', ' 只此青绿', '2026-05-30', '古装,历史', 1, '中国大陆', 0, '只此青绿，这很中国！源于中国十大传世名画之一、百年仅展出四次的宋代名画、典藏于故宫博物院的“镇馆之宝”《千里江山图》，改编自同名“爆款”舞蹈诗剧，由舞台原班编导演团队参与共同打造的电影《只此青绿》，讲述了一位现代文物研究员在《千里江山图》即将展出之际潜心钻研，跨越九百余年时空走入北宋少年画家希孟的内心，目睹了希孟呕心沥血忘我创作，与无数劳动者携手，历尽千辛万苦创作出传世画卷的历程。', 'http://127.0.0.1:8888/api/upload/view?key=2b2d38a7-d5fc-456f-98df-83a3681cf3cf.jpg', 89);
INSERT INTO `t_film` VALUES ('38843d2b9e2dcb308168ff08ee32306d', '我看见两朵一样的云', '2026-06-19', '爱情,科幻', 1, '中国大陆', 0, '崩溃了家人们！这世界疯了！日常都是bug，身边都是伪人！谁能懂懂我有多&@#…*%^（已被系统强制马赛克） 你有没有在这个世界感到过无比的孤独?有没有怀疑身边的一切是不真实的? 阿志(王源 饰)在大家眼里是一个奇怪的年轻人，他的业余生活都在寻找“这个世界是虚拟世界”的证据。直到古灵精怪的女生小一(文淇 饰)的出现，让他几乎放弃了寻找真相的念头。透过阿志和小一被快速点燃的爱情，生活显得如此浪漫而真实。可是，小一究竟是虚拟游戏的NPC、还是现实生活的完美爱人，他们能不能在这个世界上永远幸福地生活在一起?……', 'http://127.0.0.1:8888/api/upload/view?key=9ab5c463-0c76-411b-8046-8d842792583d.jpg', 104);
INSERT INTO `t_film` VALUES ('3aee3a3a443812797762520899873916', '抓特务', '2026-06-19', '犯罪,历史', 1, '中国大陆', 0, '一场警察与特务的较量，却展开满是烟火与温情的百姓人生。 新中国成立之初，派出所所长肖大力（雷佳音 饰）凭直觉锁定潜伏在土唐刀胡同15号院的特务“5182”冯静波（胡歌 饰）。为追查真相，肖大力索性搬进院落，昔日追查者与目标成了朝夕相处的邻居。 两人的交集，牵动着两个家庭、两代人的命运，岁月起落间满是波折与变故。当阅尽半生风雨，这对相伴相斗一辈子的冤家，早已不再是单纯的宿敌。', 'http://127.0.0.1:8888/api/upload/view?key=5943c904-f16e-499d-8537-9a60fe9c7663.jpg', 141);
INSERT INTO `t_film` VALUES ('412c0003d42f2e66f958936db53388a0', '重返·狼群', '2026-02-19', '纪录片', 1, '中国大陆', 0, '画家李微漪在一次草原采风中意外收养了狼王遗孤，为其取名格林，并带回成都喂养。但繁华的都市无法容纳一匹野性的草原狼，李微漪带着格林重返草原，一路追寻狼群的踪迹，历经严寒酷雪，终于目送它成功地重返狼群。', 'http://127.0.0.1:8888/api/upload/view?key=8afc497a-a850-4faf-af59-5d528db359d5.jpg', 98);
INSERT INTO `t_film` VALUES ('565c5c36cd3a208740e9e21b7f0a6937', '火遮眼', '2026-06-11', '动作,犯罪', 1, '中国香港', 0, '不靠套招，不留退路！全员狠人，打戏封神！世界五大杀神生死搏杀，招招致命！ 东南亚某处，失语维修工王伟（谢苗 饰）因女儿雨晴（杨恩又 饰）失踪觉醒猎杀本能。他联手寻妻记者纳文（林科灯 饰）组成生死同盟，在连番血战中死斗黑暗组织打手大块头（黎唯 饰）与嗜血杀手阿德（雅彦·鲁伊安 饰）一众人等。怒火遮眼，鲜血开路，从血肉翻飞的街头混战，到拳拳断骨的室内死斗，一场又一场血腥残暴的生死拼杀连番上演，与此同时雨晴也在魔窟中用父亲教的功夫自救。随着二人深入敌营，真相直指背后更庞大的黑恶势力。', 'http://127.0.0.1:8888/api/upload/view?key=3930bae1-5592-40ff-9b96-1a86f66dc3e4.jpg', 113);
INSERT INTO `t_film` VALUES ('65faffd2dec3b7ef941d56566aa4be30', '四渡', '2026-06-26', '战争,历史', 1, '中国大陆', 0, '长征时期，红军身处绝境。四渡赤水堪称红军的绝地反击之战。在毛泽东的带领下，红军一渡赤水时形势所迫、实属无奈；二渡赤水出其不意，打了敌人个措手不及；三渡赤水堪称精妙之笔，尽显战略智慧；四渡赤水更是神来之笔，操作令人叫绝。这一系列军事行动创造了战争史上的经典战例，成为运动战的传奇典范。', 'http://127.0.0.1:8888/api/upload/view?key=74175c39-a865-4197-bfd1-0ab7d331bb7f.jpg', 122);
INSERT INTO `t_film` VALUES ('72a93d16eba373ab0d24fc809d923ab2', '铃芽之旅 すずめの戸締まり', '2023-03-24', '爱情,科幻,动画', 1, '日本', 0, '    宁静的九州乡间小镇，生活着平凡的少女岩户铃芽（原菜乃华 配音）。这天上学路上，她邂逅了神秘的白衣青年宗像草太（松村北斗 配音）。草太的言行举止引起了铃芽的好奇，她随着这个青年来到了一处废墟，并看到了一扇在水中央突兀伫立的门。懵懵懂懂，铃芽无意间解开了某个古老封印。在此之 后，一场地震突发，而偏偏只有铃芽才能看见地震前发生的诡异现象。随后这个惶恐的女孩才发现，草太原来是有着古老传承的闭门师，他巡游日本各地正是为了阻止更大的灾难发生。\n　　从西日本到东日本，追逐灾难的旅途中，铃芽尘封的记忆也渐渐苏醒……', 'http://127.0.0.1:8888/api/upload/view?key=9e309dae-323b-4730-aa7f-14c27b27f605.jpg', 122);
INSERT INTO `t_film` VALUES ('75b846036c76335c34d14ef71fed781f', '超级少女', '2026-06-26', '科幻,动作,冒险', 1, '美国', 0, '当一位冷酷无情、突如其来的劲敌突袭家园，卡拉·佐-艾尔——也就是“超级少女”——不得不与一位意想不到的同伴结盟，横跨星际，踏上一段交织复仇与正义的壮阔征途。', 'http://127.0.0.1:8888/api/upload/view?key=4d7400bf-2b67-40ed-835f-25087be5cc21.jpg', 110);
INSERT INTO `t_film` VALUES ('770fda9f5cb2c180836d25d24edbacda', '熊猫计划之部落奇遇记', '2026-02-17', '喜剧,科幻', 1, '中国大陆', 0, '神秘部落规矩离奇，怪事接连不断！？熊猫胡胡和国际巨星Jackie（成龙 饰）意外穿越进入与世隔绝的原始部落！拥有大小黑眼圈的憨萌熊猫胡胡竟被奉为“天降神兽”，还被族人们予以重任，坚信她能化解部落危机。为守护胡胡，Jackie只得与怪异的族人们斗智斗勇：偶尔御风飞翔的族长（马丽 饰）、偶尔萌猛反差的勇士强杉（乔杉 饰）、偶尔神力附体的王子图鲁（于洋 饰）和全程身手不凡的公主纱依（王影璐 饰），这场奇遇又会藏着怎样的爆笑冒险？胡胡能否化解部落的危机？神秘的部落背后又会隐藏着怎样的古老秘密？', 'http://127.0.0.1:8888/api/upload/view?key=34ee2c38-d273-4311-a11c-a8c5909b0251.jpg', 100);
INSERT INTO `t_film` VALUES ('a28c2f58aa9d33c83719146306ec9ac7', '小气鬼', '2026-06-27', '喜剧', 1, '中国大陆', 0, '“小气鬼”的百味人生，笑泪交织的温暖市井烟火，包真实包逗趣包好看！ 民乐团唢呐手高艺（孟鹤堂 饰），人见人嫌，是把“省一分是一分”刻进DNA里的小气鬼。他却因贪图三百块钱，受托临时照顾4岁男孩阿乌（吴笛畅 饰），不料男孩的母亲一去不返，打破了高艺原本平静的生活，两人被迫搭伙成为“半路父子”。 与此同时，高艺对大提琴手温兰（管乐 饰）一见钟情。两人共同经历了一场惊心动魄的Livehouse炸裂演出之后，高艺向温兰展开了一系列抠搜绝伦的求爱攻势。经过一番啼笑皆非的喜笑离合，高艺前半生坚守的金钱原则和人生态度，似乎也在不经意间开始改变.....', 'http://127.0.0.1:8888/api/upload/view?key=1e7a1f8d-c819-4df6-9f1c-497cea3037fd.jpg', 113);
INSERT INTO `t_film` VALUES ('a33f89d02bdbfc832170cfa8e1885b33', '森中有林 ', '2026-05-23', '爱情,犯罪', 1, '中国大陆', 0, '        廉加海（于和伟 饰）被吕新开（韩庚 饰）意外打瞎了一只眼，没能赴约错过了爱人王秀义（高圆圆 饰）。这场意外却让他用一只眼给女儿廉婕（张天爱 饰）换来了一生的幸福。\n　　多年后，廉加海与王秀义重逢，旧情复燃，却意外卷入一场凶案。被众人联手掩盖的真相，与神秘而危险的王秀义有着千丝万缕的关系，也让两个家、三代人的命运开始了数十年的生死纠葛、爱恨痴缠……是谁杀了谁？又是谁要替谁抵命？廉加海和王秀义永远只差一点点的感情又将如何做个了断？\n　　影片改编自郑执同名小说。', 'http://127.0.0.1:8888/api/upload/view?key=6ea51795-bdc2-4bbf-a446-99a76c4eb5e2.jpg', 117);
INSERT INTO `t_film` VALUES ('a5c3c5ad3cf92f9f1264ce4cdbeef6c8', '给阿嬷的情书 ', '2026-04-30', '家庭', 1, '中国大陆', 0, '潮汕阿嬷叶淑柔一直守着平淡的日子，安享晚年。孙子晓伟因债务缠身，瞒着家人远赴泰国，寻找传闻中的亿万富豪阿公郑木生。然而，晓伟最后却带回了一个震惊整个家族的消息：阿公早已不在人世，那个与阿嬷一直在书信中谈情说爱的，竟是一个陌生人。随着晓伟的调查，一段隐藏了半世纪的感情赫然袭来，击中了阿嬷的心。', 'http://127.0.0.1:8888/api/upload/view?key=d5d8eae6-f261-4b37-9035-3700f37bc4c0.png', 118);
INSERT INTO `t_film` VALUES ('a6cc9fe1808ad045295d29c9c353ddc0', '玩具总动员5 （Toy Story 5）', '2026-06-19', '喜剧,动画,儿童', 1, '美国', 0, '玩具们将会跟孩子们如今沉迷的电子产品展开激烈较量', 'http://127.0.0.1:8888/api/upload/view?key=7743c374-ed66-4cb7-b127-f24b46937e8e.jpg', 102);
INSERT INTO `t_film` VALUES ('a7391218ec253fbcea19311da738dc4e', '揭秘日', '2026-06-12', '科幻', 1, '美国', 0, '一部关于UFO、外星人和“真相”的科幻片，被“附体”的主播，与未知的相遇充满神秘和悬疑。 “70亿人有权知道真相。……我要向全世界、一次、完全揭露。” “他造出了广袤的宇宙，怎么会只留给我们人类？”', 'http://127.0.0.1:8888/api/upload/view?key=8995060b-b085-40d7-b33e-01a23bfacf55.jpg', 146);
INSERT INTO `t_film` VALUES ('ab5c542092eebd9b89af1b69cda7233f', '澎湖海战', '2026-07-25', '动作,历史,战争', 1, '中国大陆', 0, '公元1681年，台湾权臣冯锡范刘国轩弑杀监国郑克臧，挟立12岁郑克塽为新王，冯刘集团加剧分疆裂土以武谋独进程，并长期袭扰东南沿海城镇。康熙皇帝（易烊千玺 饰）多次派员招抚未果，于公元1683年，命福建水师提督施琅（王学圻 饰）领兵平台。施琅于澎湖大败台湾水师，最终以武止戈，实现了台湾和平统一。', 'http://127.0.0.1:8888/api/upload/view?key=af28570a-b0e3-43c1-b285-fd7c4ca13c03.jpg', 144);
INSERT INTO `t_film` VALUES ('c22d11be21f33479c237a244356fd809', '蜘蛛侠：崭新之日 ', '2026-07-29', '冒险,动作,科幻', 1, '美国', 0, '       彼得·帕克（汤姆·霍兰德 饰）自愿让全世界遗忘自己，转眼已是四年。失去挚友、挚爱与亲人的陪伴，他独自重返街头，以全职蜘蛛侠的身份坚守使命，守护整座城市。\n　　然而，日益沉重的责任与无尽压力，意外引发了一场骇人的身体异变，直接威胁到蜘蛛侠的生命，迫使他向“绿巨人”班纳博士寻求帮助。与此同时，多方邪恶势力趁虚而入，纽约再度陷入危机。面对前所未有的强敌，蜘蛛侠能否绝境破局，完成破茧重生？', 'http://127.0.0.1:8888/api/upload/view?key=f3671fb6-9b32-4827-ae98-0e8a3ca6b689.jpg', 150);
INSERT INTO `t_film` VALUES ('c87dce9698d45636a991e8596207d76d', '坚毅奇兵', '2025-02-06', '惊悚,动作', 1, '印度', 0, '一对已婚夫妇的旅行遭遇了令人不安的转折，妻子失踪后，丈夫疯狂寻找，但一个神秘反派为他创造了连连不断的障碍。', 'http://127.0.0.1:8888/api/upload/view?key=9800fb66-5550-4202-9c76-a229ad59a416.jpg', 150);
INSERT INTO `t_film` VALUES ('d8843c0b78e5a5bf8c9b176d8549759f', '惊蛰无声 ', '2026-02-17', '犯罪', 1, '中国大陆', 0, '你看不见的地方，谍战从未停止交锋！我国最新战机的涉密信息外泄，国家安全面临威胁，国安小组迅速行动抓捕间谍，在信任与背叛的漩涡中，一场惊心博弈悄然上演……', 'http://127.0.0.1:8888/api/upload/view?key=74331602-7a35-4804-88d7-fa0ef5fc2dd4.jpg', 104);
INSERT INTO `t_film` VALUES ('ddc3a73a828a5d653cc4a88636ba62e0', '消失的人', '2026-05-01', '犯罪,惊悚,悬疑', 1, '中国大陆', 0, '五一必看高口碑惊悚爽片！郑恺刘浩存邱泽身陷楼宇间的连环疑案，凶手踪迹难寻，人性良知难测！ 一个寻常的清晨，唐宇（郑恺 饰）的儿子在楼梯间凭空消失；隔壁单元里，独居女孩林雨彤（刘浩存 饰）在熟睡中被人侵犯；赌鬼租客严午（邱泽 饰）满身血污，谎编踪迹藏匿尸体。这栋看似平静、不起眼的居民楼里，每个擦肩而过的“熟人”，都有可能是藏着獠牙的恶鬼。当人心比鬼更可怕时，你每天出入的家门，都可能是通往地狱的入口……', 'http://127.0.0.1:8888/api/upload/view?key=d41c4bc8-f327-4108-accf-009d48c08940.jpg', 140);
INSERT INTO `t_film` VALUES ('df5f178c6b99f887f9a43b20ca415416', '你想活出怎样的人生 君たちはどう生きるか', '2024-04-03', '科幻,动画,冒险', 1, '日本', 0, '       少年牧真人（山时聪真 配音）的母亲葬身火海后，他随父亲牧胜一（木村拓哉 配音）与继母夏子（木村佳乃 配音）组成新家庭。深陷悲伤的真人阴郁孤僻，难以融入新环境。一次意外，他跟随一只会说话的苍鹭（菅田将晖 配音）闯入废弃的神秘塔楼，却不料进入了奇幻的“亡灵世界”，开始了一场不可思议的冒险……\n　　影片片名致敬了小说家吉野源三郎在1937年发表的同名小说，但电影故事由宫崎骏原创。', 'http://127.0.0.1:8888/api/upload/view?key=b9029691-b04b-451e-b6cf-a330c1f5dc91.jpg', 124);
INSERT INTO `t_film` VALUES ('e6db3812245e74ca0c397ae58cd9bf54', '西线无战事 Im Westen nichts Neues ', '2022-09-12', '动作,战争', 1, '德国', 0, '该片改编自埃里希·玛利亚·雷马克撰写的同名经典小说，讲述在第一次世界大战期间，保罗·鲍曼和同学莱尔、米勒参加了德国国民志愿兵，被派往前线与法军作战。在战场上，他们目睹到了残酷的饥饿、血腥和死亡，战争的荒谬无情让保罗从迷恋战争到心灰意冷。', 'http://127.0.0.1:8888/api/upload/view?key=2bb614d0-a27b-49b9-aee6-b6b301e196c3.jpg', 148);
INSERT INTO `t_film` VALUES ('ec8b7b600d6c2639d4449632352d72ad', '阴风诡影', '2026-06-19', '恐怖,惊悚', 1, '中国大陆', 0, '午夜墓场，女鬼爬出坟墓！阴风阵阵，鬼影飘飘，野火隐隐，怪鸟鸣叫，谁敢踏入阴阳交汇的追魂场？！当主人公午夜探路，寻找种种诡异真相，那狰狞鬼面突然出现，揪心鬼笑飘忽响起，夺命的利爪已经插入胸口！惨叫连连，鲜血四溅！是冤死的妻子诈尸还魂？还是内心的魔咒幻化成厉鬼？亦或是坟场的千年诅咒打开了怨灵的禁忌牢笼？午夜惊魂时刻，当恐怖鬼影现了真身，所有人必将不寒而栗！魂飞魄散！……', 'http://127.0.0.1:8888/api/upload/view?key=64f86a5b-7660-4119-97a1-7b764b5e6aa7.jpg', 88);
INSERT INTO `t_film` VALUES ('ee7fbbb3404253fdaa254175bc1fa258', '熊出没·年年有熊 ', '2026-02-17', '喜剧,动画,科幻', 1, '中国大陆', 0, '熊大曾是森林里的“老大哥”，直到一个不速之客到来，它将自己神力传给了熊强，熊大变成了三人组合内能力最弱者。为了改变现状，他步入了反派陷阱，引发了毁天灭地的危机......', 'http://127.0.0.1:8888/api/upload/view?key=5f89d3c3-0576-4d6e-95bb-f9e6682320f6.jpg', 118);
INSERT INTO `t_film` VALUES ('ef498b7cbc2fe2deebe74cdceb7772cd', '镖人：风起大漠', '2026-02-17', '动作,古装,武侠', 1, '中国大陆', 0, '大漠之上，镖人、官府、西域五大家族等多方势力盘根错节、暗潮涌动。“天字第二号逃犯”刀马接下特殊押镖任务，和同伴一起从西域护镖远赴长安。不料，他们的护送对象竟是“天字第一号逃犯”知世郎……天下熙熙皆为利来，各方势力闻风入局，抢镖厮杀接连上演……', 'http://127.0.0.1:8888/api/upload/view?key=3d8cc8ae-d3bd-4bd2-9f2e-19a5a95ab215.jpg', 126);
INSERT INTO `t_film` VALUES ('f38d31192d75191d4a257269344aa9ed', '超级马力欧银河大电影', '2026-04-03', '喜剧,动画,冒险,动作', 1, '美国', 0, '延续13亿美元票房口碑的《超级马力欧兄弟大电影》，原班团队回归打造《超级马力欧银河大电影》，开启“银河级”全新冒险。由照明娱乐×任天堂联合制作，环球影业全球发行。', 'http://127.0.0.1:8888/api/upload/view?key=71ffc24b-c5da-46ca-ac15-0709e5b89fdb.jpg', 98);
INSERT INTO `t_film` VALUES ('fdad9cba2c14c4f1c744a3ccb57b666b', '飞驰人生3 ', '2026-02-17', '喜剧,冒险', 1, '中国大陆', 0, '巴音布鲁克最后一站收官后，张驰（沈腾 饰）受邀作为车队主教练征战全新赛事“沐尘100拉力赛”，“野生车手”走上国际舞台！面对高手如云的全新赛道，孙宇强（尹正 饰）、记星（张本煜 饰）一如既往协同作战，林臻东（黄景瑜 饰）等实力车手应邀强势集结，一支凝聚了顶配速度与信念的车队就此成立！然而，张驰发现真正的挑战仿佛并非来自比赛本身，赛场之外暗流涌动，让他们飞驰之路充满变数……', 'http://127.0.0.1:8888/api/upload/view?key=efe67911-4f87-40c0-aae5-9e9801aa5e44.jpg', 126);

-- ----------------------------
-- Table structure for t_film_evaluate
-- ----------------------------
DROP TABLE IF EXISTS `t_film_evaluate`;
CREATE TABLE `t_film_evaluate`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `fid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `uid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `star` int NOT NULL,
  `comment` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_film_evaluate
-- ----------------------------
INSERT INTO `t_film_evaluate` VALUES ('03d0eb7e6eb3c56e6688c039d403574e', '7805ad5309cd299e6a9cd207885c110e', 'b8cd70cd-4c8e-4ed2-a365-94740053807b', 4, '', '2026-05-03 18:47:37');
INSERT INTO `t_film_evaluate` VALUES ('0de9b0a9582d2304e35abbc1ffeb0103', '7555aa5256f2b00312d1bdf3c81e0ba1', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', 5, '', '2026-05-02 21:32:31');
INSERT INTO `t_film_evaluate` VALUES ('3e88d1b6f1aa5a62fb5354db78e200be', 'dbd62f019c06c94a1994e282905b5cc5', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', 4, '', '2026-05-02 21:32:14');
INSERT INTO `t_film_evaluate` VALUES ('46c902f50e0019b5ed6a95c59d82d46e', '99e5885d66cc8b69893294ae73132920', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', 5, '', '2026-05-03 17:44:15');
INSERT INTO `t_film_evaluate` VALUES ('8aac36dddb41cdfd797d22c7867901cf', '3a803a8725db3268a99a6dc083247312', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', 5, '', '2026-05-02 21:33:06');
INSERT INTO `t_film_evaluate` VALUES ('c577ec0f210452a2ffc71f7bfc3d6fb6', '3cc2178c1581c7cd680bdd33d6d2522d', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', 4, '', '2026-05-03 18:46:32');
INSERT INTO `t_film_evaluate` VALUES ('d51e09a3abf21c4809cff60a0b0da3b4', '3a803a8725db3268a99a6dc083247312', '7e4b3010-ce19-436b-9683-a7d2037dded7', 5, '不错啊', '2021-03-31 16:48:51');
INSERT INTO `t_film_evaluate` VALUES ('f6d3b74d3d05517f07096b8c5d53b1b2', '7805ad5309cd299e6a9cd207885c110e', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', 3, '', '2026-05-03 18:47:06');

-- ----------------------------
-- Table structure for t_leaving_message
-- ----------------------------
DROP TABLE IF EXISTS `t_leaving_message`;
CREATE TABLE `t_leaving_message`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `uid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `reply` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_leaving_message
-- ----------------------------
INSERT INTO `t_leaving_message` VALUES ('265eeb3f1302775a922330c59f9a9bfe', 'b8cd70cd-4c8e-4ed2-a365-94740053807b', '555', 'null', '2026-05-03 14:50:34');
INSERT INTO `t_leaving_message` VALUES ('2a49df7b264e0dbd2ed255172df9bf40', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '哈哈哈', '哈哈哈', '2026-05-02 21:08:00');
INSERT INTO `t_leaving_message` VALUES ('7e8401b3092d5465a59c009e51f486a4', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '针不戳', 'null', '2026-05-02 21:08:30');
INSERT INTO `t_leaving_message` VALUES ('dc1b01e2c2b31c0d80767873317db073', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '哈哈哈哈', '', '2026-06-09 17:04:57');
INSERT INTO `t_leaving_message` VALUES ('e4a151c2cd592e02ab211efbef25cf7c', '1db44f28-c2ac-470b-a889-56ed14ab7202', '系统做的不错啊', 'null', '2021-03-31 17:09:01');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `uid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `aid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `seats` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `status` int NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `pay_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- status: 0待支付 1支付失败 2支付成功 3已撤销 4已退款
-- ----------------------------
INSERT INTO `t_order` VALUES ('a0010001-0001-4000-8000-000000000001', '7e4b3010-ce19-436b-9683-a7d2037dded7', '13833335555', '13', '4号5号', 61.00, 2, '2026-06-14 18:20:00', '2026-06-14 18:21:15');
INSERT INTO `t_order` VALUES ('a0010002-0002-4000-8000-000000000002', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '13639677035', '20', '8号9号10号', 114.00, 2, '2026-06-13 19:10:00', '2026-06-13 19:12:30');
INSERT INTO `t_order` VALUES ('a0010003-0003-4000-8000-000000000003', '1db44f28-c2ac-470b-a889-56ed14ab7202', '15537739989', '25', '15号', 35.00, 2, '2026-06-14 15:30:00', '2026-06-14 15:31:08');
INSERT INTO `t_order` VALUES ('a0010004-0004-4000-8000-000000000004', 'b8cd70cd-4c8e-4ed2-a365-94740053807b', '18678945632', '30', '1号2号', 84.00, 2, '2026-06-07 18:40:00', '2026-06-07 18:42:00');
INSERT INTO `t_order` VALUES ('a0010005-0005-4000-8000-000000000005', '7e4b3010-ce19-436b-9683-a7d2037dded7', '13833335555', '17', '20号21号', 64.00, 2, '2026-06-07 15:00:00', '2026-06-07 15:02:18');
INSERT INTO `t_order` VALUES ('444c270f-afa2-4a18-89c5-55aefdcd66e3', '7e4b3010-ce19-436b-9683-a7d2037dded7', '13833335555', '14', '4号5号', 61.00, 2, '2026-06-17 13:08:45', '2026-06-17 13:09:04');
INSERT INTO `t_order` VALUES ('a0010006-0006-4000-8000-000000000006', '1db44f28-c2ac-470b-a889-56ed14ab7202', '15537739989', '22', '11号', 38.00, 2, '2026-06-17 11:20:00', '2026-06-17 11:21:30');
INSERT INTO `t_order` VALUES ('a0010007-0007-4000-8000-000000000007', 'b8cd70cd-4c8e-4ed2-a365-94740053807b', '18678945632', '31', '5号6号', 84.00, 2, '2026-06-17 12:00:00', '2026-06-17 12:01:45');
INSERT INTO `t_order` VALUES ('a0010008-0008-4000-8000-000000000008', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '13639677035', '44', '3号4号', 76.00, 2, '2026-06-17 10:30:00', '2026-06-17 10:32:00');
INSERT INTO `t_order` VALUES ('a0010009-0009-4000-8000-000000000009', 'b8cd70cd-4c8e-4ed2-a365-94740053807b', '18678945632', '26', '8号', 35.00, 0, '2026-06-17 15:40:00', NULL);
INSERT INTO `t_order` VALUES ('a0010010-0010-4000-8000-000000000010', '1db44f28-c2ac-470b-a889-56ed14ab7202', '15537739989', '43', '16号17号', 70.00, 0, '2026-06-17 15:35:00', NULL);
INSERT INTO `t_order` VALUES ('a0010011-0011-4000-8000-000000000011', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '13639677035', '38', '9号', 36.00, 1, '2026-06-17 09:00:00', NULL);
INSERT INTO `t_order` VALUES ('a0010012-0012-4000-8000-000000000012', '7e4b3010-ce19-436b-9683-a7d2037dded7', '13833335555', '21', '22号', 38.00, 1, '2026-06-16 20:00:00', NULL);
INSERT INTO `t_order` VALUES ('65ef145e-20b5-4d2b-a259-ab02c97a7ab4', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '13639677035', '15', '10号', 30.50, 4, '2026-06-17 13:20:23', '2026-06-17 13:26:29');
INSERT INTO `t_order` VALUES ('a0010013-0013-4000-8000-000000000013', '1db44f28-c2ac-470b-a889-56ed14ab7202', '15537739989', '32', '7号8号', 84.00, 4, '2026-06-16 10:00:00', '2026-06-16 10:05:00');
INSERT INTO `t_order` VALUES ('a0010014-0014-4000-8000-000000000014', 'b8cd70cd-4c8e-4ed2-a365-94740053807b', '18678945632', '29', '2号', 32.00, 3, '2026-06-16 14:00:00', NULL);

-- ----------------------------
-- Table structure for t_order_exception
-- ----------------------------
DROP TABLE IF EXISTS `t_order_exception`;
CREATE TABLE `t_order_exception`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `oid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` tinyint NOT NULL,
  `result` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `reviewer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `end_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_exception
-- status: 0待处理 1已处理
-- ----------------------------
INSERT INTO `t_order_exception` VALUES ('ex001-0001-4000-8000-000000000001', 'a0010012-0012-4000-8000-000000000012', '支付超时后座位仍显示占用，用户无法重新选座', 1, '已核实为系统锁座延迟，已手动释放座位并联系用户重新购票', 'admin', '2026-06-16 20:30:00', '2026-06-16 21:00:00');
INSERT INTO `t_order_exception` VALUES ('ex002-0002-4000-8000-000000000002', '65ef145e-20b5-4d2b-a259-ab02c97a7ab4', '用户申请退款：临时有事无法观影', 1, '支付宝沙箱原路退款成功，座位已释放', 'admin', '2026-06-17 13:25:00', '2026-06-17 13:30:00');
INSERT INTO `t_order_exception` VALUES ('ex003-0003-4000-8000-000000000003', 'a0010009-0009-4000-8000-000000000009', '用户反馈支付宝支付页面一直加载失败', 0, NULL, 'worker1', '2026-06-17 15:42:00', NULL);
INSERT INTO `t_order_exception` VALUES ('ex004-0004-4000-8000-000000000004', 'a0010011-0011-4000-8000-000000000011', '订单超时扣款但状态未更新，用户称已扣款', 0, NULL, 'worker2', '2026-06-17 09:30:00', NULL);
INSERT INTO `t_order_exception` VALUES ('ex005-0005-4000-8000-000000000005', 'a0010014-0014-4000-8000-000000000014', '场次时间调整，管理员撤销订单后用户未收到通知', 1, '已短信通知用户并补偿一张优惠券（演示）', 'admin', '2026-06-16 15:00:00', '2026-06-16 16:30:00');

-- ----------------------------
-- Table structure for t_poster
-- ----------------------------
DROP TABLE IF EXISTS `t_poster`;
CREATE TABLE `t_poster`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` tinyint NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_poster
-- ----------------------------
INSERT INTO `t_poster` VALUES ('17dd40c6-3036-4eab-bef0-f236c3fa0ed9', '给阿嫲的情书', 'http://127.0.0.1:8888/api/upload/view?key=e16362d6-8e2e-4d3f-8175-7be029417043.jpg', 1, '2026-06-17 11:02:03');
INSERT INTO `t_poster` VALUES ('557c55ec-9052-4cc8-a3d7-1dc5fa267612', '爱是愤怒', 'http://127.0.0.1:8888/api/upload/view?key=52cf4fdb-bc90-46fe-abcb-402c5b3a2400.jpg', 1, '2026-06-17 11:07:57');
INSERT INTO `t_poster` VALUES ('96172260-4b53-4fe5-a48f-1a86be57717b', '消失的人', 'http://127.0.0.1:8888/api/upload/view?key=38712fa8-aa18-4939-ab70-e6dc3074a648.jpg', 1, '2026-06-17 11:03:20');
INSERT INTO `t_poster` VALUES ('da4a1272-7499-4faa-b200-96fd7ba40e00', '火遮眼', 'http://127.0.0.1:8888/api/upload/view?key=80099405-ad46-452a-a88d-06c31f4b2116.jpg', 1, '2026-06-17 11:04:58');

-- ----------------------------
-- Table structure for t_registration
-- ----------------------------
DROP TABLE IF EXISTS `t_registration`;
CREATE TABLE `t_registration`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `aid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `uid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_registration
-- ----------------------------
INSERT INTO `t_registration` VALUES ('0d272a334eaea17b2d6ecb63da23530f', 'c0932fdf-c8b2-4ef3-a186-e80287364c0a', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '2026-06-10 09:02:45');
INSERT INTO `t_registration` VALUES ('c403d850d84dd388e44ce15f8b5de192', '4d0667de-c19b-450f-b3dd-e43322c6dbda', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '2026-05-03 12:31:56');

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `wid` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('174c5d2a-62b9-465e-83de-d83cd884ed19', 'e9cb1e2e-6cef-427d-a7d0-d2015f848051', 'ROLE_WORKER', '2026-06-17 13:12:34');
INSERT INTO `t_role` VALUES ('296fe453-0ef0-42d7-94a7-76c565534b08', '539385ec-ef39-4f97-b7f6-caffc9b80f41', 'ROLE_ORDER_MANAGE', '2026-06-08 16:52:48');
INSERT INTO `t_role` VALUES ('3d1cdbbf-a6cd-4b05-8819-fb52f48d2083', '96e40d13-b5d1-4f05-ad97-e5845b394302', 'ROLE_WORKER', '2021-03-31 16:58:23');
INSERT INTO `t_role` VALUES ('48a796a1-a7ff-43d7-a668-91194d047a8f', '96e40d13-b5d1-4f05-ad97-e5845b394302', 'ROLE_WORKER', '2026-05-02 17:40:24');
INSERT INTO `t_role` VALUES ('582b4660-bef5-4377-a855-13cd53eaefe6', '593f3276-abb4-42b0-a0f4-f6cc699f6e44', 'ROLE_WORKER', '2026-06-17 13:11:28');
INSERT INTO `t_role` VALUES ('b231b008-b56e-4dd4-baa9-8d299fab8048', '539385ec-ef39-4f97-b7f6-caffc9b80f41', 'ROLE_WORKER', '2021-03-31 16:30:34');
INSERT INTO `t_role` VALUES ('dbefe46a-bdaa-44e0-b7c2-c3eece8017d7', '539385ec-ef39-4f97-b7f6-caffc9b80f41', 'ROLE_FILM_MANAGE', '2026-05-03 11:42:26');
INSERT INTO `t_role` VALUES ('eb4a7e70-c64e-435b-92ce-4d64f81de49c', '', 'ROLE_USER_VIEW', '2026-06-17 12:50:25');
INSERT INTO `t_role` VALUES ('f7e9e03e-ed0b-4d79-b3e6-6d621ccf8e2f', '539385ec-ef39-4f97-b7f6-caffc9b80f41', 'ROLE_USER_VIEW', '2026-05-02 21:04:06');
INSERT INTO `t_role` VALUES ('fe3ec4ff-a89d-45dd-8567-0cd0f0ca7784', '', 'ROLE_ORDER_MANAGE', '2026-06-17 12:51:17');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'User',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `birthday` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `gender` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `info` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_at` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `update_at` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1db44f28-c2ac-470b-a889-56ed14ab7202', 'user1', '$2a$10$TaVeWusf.ykOWqQMx.h8GOsj029q0IxbB5tLLgttHWKyJUTMNP74G', 'http://127.0.0.1:8888/api/upload/view?key=315a6066-d043-4ddb-9972-4467b90649e5.jpg', '枫叶离', '15537739989', '2004-10-26', '男生', NULL, '2021-03-31 17:07:00', '2026-06-17 12:26:20');
INSERT INTO `t_user` VALUES ('7e4b3010-ce19-436b-9683-a7d2037dded7', 'user3', '$2a$10$TaVeWusf.ykOWqQMx.h8GOsj029q0IxbB5tLLgttHWKyJUTMNP74G', 'http://127.0.0.1:8888/api/upload/view?key=549f1f25-eab7-4240-bd81-baf609401d5d.jpg', '奶油刘啊刘', '13833335555', '2003-06-03', '女生', NULL, '2021-03-31 16:11:17', '2026-06-17 12:29:57');
INSERT INTO `t_user` VALUES ('b8cd70cd-4c8e-4ed2-a365-94740053807b', 'user2', '$2a$10$diYY9a96sma5KYIlLH3y/.YlJ.HGSONedj0fvOZrEiAamvBilNEoe', 'http://127.0.0.1:8888/api/upload/view?key=bee4e5e2-1b18-4713-b91d-89d593bb51a5.jpg', '俗人', '18678945632', '2005-10-03', '男生', NULL, '2026-05-03 12:36:31', '2026-06-17 12:27:23');
INSERT INTO `t_user` VALUES ('dfa462f4-bf0b-40e8-bccf-180f4730b682', 'user4', '$2a$10$.Jg4Bp1aVpgpXoE.Jt2Lk.FImawFX15e2zdoErBXabJdQaovvP4/K', 'http://127.0.0.1:8888/api/upload/view?key=617b5f76-e10b-4ed3-8646-2ac0f0532d4f.jpg', '逆光', '13639677035', '2010-05-01', '男生', '快乐是选择。', '2026-05-02 16:39:08', '2026-06-17 12:32:02');

-- ----------------------------
-- Table structure for t_worker
-- ----------------------------
DROP TABLE IF EXISTS `t_worker`;
CREATE TABLE `t_worker`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `entry` tinyint NOT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `update_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_worker
-- ----------------------------
INSERT INTO `t_worker` VALUES ('539385ec-ef39-4f97-b7f6-caffc9b80f41', 'worker2', '$2a$10$xp.GqmvfVCDs73jMcvA.le8Cl.sZ6ALXD8qzL8UDPk4XcSDEdUDgW', '小新', '13633333333', 1, '女生', 'http://127.0.0.1:8888/api/upload/view?key=93f6d531-8994-49f0-9a0a-c1d73d3bcef5.jpg', '2021-03-31 16:30:34', '2026-06-17 12:16:30');
INSERT INTO `t_worker` VALUES ('593f3276-abb4-42b0-a0f4-f6cc699f6e44', 'worker3', '$2a$10$PZtSOBpzEcbH1GLh2w8BWO9bLc756vtB/J2V0xHHmfJ1iho/xJBfm', '小倩', '15845645641', 1, '女', 'http://127.0.0.1:8888/api/upload/view?key=26ec41ac-a9eb-4436-b193-1a817eca4eb0.jpg', '2026-06-17 13:11:28', '2026-06-17 13:11:28');
INSERT INTO `t_worker` VALUES ('96e40d13-b5d1-4f05-ad97-e5845b394302', 'worker1', '$2a$10$TaVeWusf.ykOWqQMx.h8GOsj029q0IxbB5tLLgttHWKyJUTMNP74G', '小华', '13644444444', 1, '女', 'http://127.0.0.1:8888/api/upload/view?key=e1d1c35d-2be8-4a18-9e9b-1f7a7a357d66.jpg', '2021-03-31 16:58:23', '2026-06-17 12:14:16');
INSERT INTO `t_worker` VALUES ('e9cb1e2e-6cef-427d-a7d0-d2015f848051', 'worker4', '$2a$10$2m7eKOm1kiGoY84kAHQBleMQLLZe5tjciFS4tgFhQnSS4RNVQpJK.', '小梦', '17502120000', 1, '女', 'http://127.0.0.1:8888/api/upload/view?key=cfbe558d-6117-40ae-b0b6-151030ec274f.jpg', '2026-06-17 13:12:34', '2026-06-17 13:12:34');

-- ----------------------------
-- Table structure for t_worker_evaluate
-- ----------------------------
DROP TABLE IF EXISTS `t_worker_evaluate`;
CREATE TABLE `t_worker_evaluate`  (
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `wid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `uid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_worker_evaluate
-- ----------------------------
INSERT INTO `t_worker_evaluate` VALUES ('2d5aad12-d51e-451e-b74c-24e902d33542', '539385ec-ef39-4f97-b7f6-caffc9b80f41', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', 'ai', '不满意', '2026-06-09 17:10:12');
INSERT INTO `t_worker_evaluate` VALUES ('509dd54f-0558-47f1-b5db-8332f6769262', '539385ec-ef39-4f97-b7f6-caffc9b80f41', '1db44f28-c2ac-470b-a889-56ed14ab7202', '不错', '非常满意', '2021-03-31 17:08:54');
INSERT INTO `t_worker_evaluate` VALUES ('ec15d9c2-86e3-451a-bec9-b8a3112d3bb5', '96e40d13-b5d1-4f05-ad97-e5845b394302', 'dfa462f4-bf0b-40e8-bccf-180f4730b682', '123', '满意', '2026-05-02 21:28:17');

SET FOREIGN_KEY_CHECKS = 1;
