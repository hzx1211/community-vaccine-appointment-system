/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306_2
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : vaccine_system2

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 12/03/2026 14:27:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'community_admin' COMMENT '角色：admin-系统管理员，community_admin-社区管理员',
  `community_id` bigint NULL DEFAULT NULL COMMENT '所属社区ID（社区管理员关联）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_community_id`(`community_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$zfsjc.FMzqnXtP4AEjQN6uZtjd2hGdDopxzhMTE/lGJYEyyx8xdwi', 'a', 'a', NULL, '/uploads/2025/12/19/eda047bbd1904fe99c40472143dd291c.png', 'admin', NULL, 1, '2025-12-17 17:17:15', '2025-12-19 10:07:00');
INSERT INTO `admin` VALUES (2, 'a1', '$2a$10$k1nQdL.XF1NPO4mtI6y4fuQO1fETrdEZDhn/TFEjF/Kj8t6DDkoyW', 'a', 'a', '2294994453@qq.com', '/uploads/2025/12/17/0b29273081e043188caee2c2450d6d59.png', 'community_admin', 1, 1, '2025-12-17 17:27:24', '2025-12-19 10:41:26');
INSERT INTO `admin` VALUES (3, 'b', '$2a$10$KjM3CUbUOIdN2dksk0HVLe5ez.SiVhdw0ZhmncvXQIEfsTH4WdqUC', 'b', '1111111111111111', '2294994453@qq.com', '/uploads/2025/12/17/bdf6b7ddefcf48c78b4fb6085dc2781c.jpg', 'community_admin', 2, 1, '2025-12-17 17:39:49', '2025-12-17 23:45:51');
INSERT INTO `admin` VALUES (4, 'community1', '$2a$10$43N2oM8eJheFVvVPtRTZveiTmQe.xtt56yKwFbTtaio6wtul4CNLC', '张明华', '13800001001', NULL, NULL, 'community_admin', 21, 1, '2026-01-03 23:12:41', '2026-01-03 23:17:48');
INSERT INTO `admin` VALUES (5, 'community2', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Zf4iu9S3.yCkqOPjcPXmq', '李秀英', '13800001002', NULL, NULL, 'community_admin', 17, 1, '2026-01-03 23:12:41', '2026-01-04 15:58:15');
INSERT INTO `admin` VALUES (6, 'community3', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Zf4iu9S3.yCkqOPjcPXmq', '王建国', '13800001003', NULL, NULL, 'community_admin', 3, 1, '2026-01-03 23:12:41', '2026-01-03 23:12:41');
INSERT INTO `admin` VALUES (7, 'community4', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Zf4iu9S3.yCkqOPjcPXmq', '刘芳', '13800001004', NULL, NULL, 'community_admin', 4, 1, '2026-01-03 23:12:41', '2026-01-03 23:12:41');
INSERT INTO `admin` VALUES (8, 'community5', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Zf4iu9S3.yCkqOPjcPXmq', '陈伟', '13800001005', NULL, NULL, 'community_admin', 5, 1, '2026-01-03 23:12:41', '2026-01-03 23:12:41');

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (2, 'wwwww', 'wwwwww', 0, 1, '2025-12-17 17:37:55', '2025-12-17 17:37:55');
INSERT INTO `announcement` VALUES (3, '关于2026年春节期间疫苗接种服务安排的通知', '尊敬的各位居民：\n\n2026年春节期间（1月28日-2月4日），各社区疫苗接种点将调整服务时间：\n\n1. 1月28日（除夕）至1月30日（初二）：暂停服务\n2. 1月31日（初三）起恢复正常服务\n3. 急需接种的居民请提前预约\n\n祝大家新春快乐，身体健康！\n\n社区疫苗预约管理中心\n2026年1月15日', 0, 1, '2026-01-05 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (4, '新冠疫苗加强针接种通知', '根据国家卫健委最新指导意见，符合条件的居民可预约接种新冠疫苗加强针。\n\n接种条件：\n1. 已完成基础免疫满6个月\n2. 年龄18周岁以上\n3. 无接种禁忌症\n\n请携带身份证和接种凭证前往就近社区接种点。', 0, 1, '2026-01-02 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (5, '流感疫苗到货通知', '好消息！2025-2026年度流感疫苗已到货，现开放预约。\n\n建议优先接种人群：\n- 60岁以上老年人\n- 6月龄-5岁儿童\n- 慢性病患者\n- 医务人员\n\n数量有限，请尽快预约！', 0, 1, '2025-12-31 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (6, '系统升级维护通知', '为提供更好的服务体验，本系统将于2026年1月10日凌晨2:00-6:00进行升级维护。\n\n维护期间：\n- 暂停在线预约服务\n- 已有预约不受影响\n- 紧急情况请拨打服务热线\n\n给您带来不便，敬请谅解。', 0, 1, '2025-12-29 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (7, 'HPV疫苗预约须知', '关于九价HPV疫苗预约的重要提示：\n\n1. 适用年龄：9-45周岁女性\n2. 接种程序：0-2-6月，共3针\n3. 预约方式：本平台在线预约\n4. 费用说明：每针约1330元\n\n温馨提示：疫苗供应紧张，请耐心等待排队。', 0, 1, '2025-12-26 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (8, '儿童疫苗接种时间表更新', '根据最新《国家免疫规划疫苗儿童免疫程序》，部分疫苗接种时间有调整：\n\n主要变化：\n1. 脊灰疫苗接种程序优化\n2. 麻腮风疫苗加强针时间调整\n3. 新增水痘疫苗推荐\n\n详情请咨询各社区接种点。', 0, 1, '2025-12-24 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (9, '疫苗接种注意事项', '接种疫苗前后注意事项：\n\n接种前：\n- 保持良好休息\n- 如实告知健康状况\n- 携带相关证件\n\n接种后：\n- 留观30分钟\n- 保持接种部位清洁\n- 避免剧烈运动\n- 如有不适及时就医', 0, 1, '2025-12-21 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (10, '在线支付功能上线通知', '为方便居民缴费，本平台已开通在线支付功能。\n\n支持方式：\n- 账户余额支付\n- 在线充值\n\n使用流程：\n1. 预约审核通过后\n2. 进入\"我的预约\"页面\n3. 点击\"支付\"完成缴费\n\n如有问题请联系客服。', 0, 1, '2025-12-18 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (11, '老年人免费流感疫苗接种活动', '为关爱老年人健康，本市开展65岁以上老年人免费流感疫苗接种活动。\n\n活动时间：2025年10月-2026年2月\n接种对象：本市户籍65周岁以上老年人\n所需材料：身份证、户口本\n\n请就近社区预约接种。', 0, 1, '2025-12-16 11:49:24', '2026-01-05 11:49:24');
INSERT INTO `announcement` VALUES (12, '预约取消规则说明', '为合理利用疫苗资源，现对预约取消规则说明如下：\n\n1. 预约成功后可在接种前24小时取消\n2. 已支付订单取消后自动退款至账户余额\n3. 多次无故爽约将影响后续预约\n\n请合理安排时间，珍惜预约机会。', 0, 1, '2025-12-11 11:49:24', '2026-01-05 11:49:24');

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '预约单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `vaccine_id` bigint NOT NULL COMMENT '疫苗ID',
  `community_id` bigint NOT NULL COMMENT '接种社区ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '预约时段',
  `dose_number` int NULL DEFAULT 1 COMMENT '第几剂',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待审核，1-审核通过，2-审核拒绝，3-已支付，4-已接种，5-已取消',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `vaccinate_time` datetime NULL DEFAULT NULL COMMENT '接种时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_vaccine_id`(`vaccine_id` ASC) USING BTREE,
  INDEX `idx_community_id`(`community_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_appointment_date`(`appointment_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1, '2001223766478802944', 3, 2, 1, '2025-12-24', '09:00-12:00', 1, 1000000.00, 1, NULL, NULL, NULL, '', '2025-12-17 17:31:41', '2025-12-17 17:32:24');
INSERT INTO `appointment` VALUES (2, '2001224321800458240', 2, 2, 1, '2025-12-24', '09:00-12:00', 1, 1000000.00, 5, NULL, NULL, NULL, '', '2025-12-17 17:33:53', '2025-12-19 11:36:14');
INSERT INTO `appointment` VALUES (3, '2001226709181517824', 2, 1, 2, '2025-12-17', '14:00-17:00', 1, 10000.00, 4, NULL, '2025-12-17 17:44:31', '2025-12-17 17:44:38', '', '2025-12-17 17:43:23', '2025-12-17 17:44:37');
INSERT INTO `appointment` VALUES (4, '2001851112328130560', 2, 3, 1, '2025-12-31', '14:00-17:00', 2, 3.00, 4, NULL, '2025-12-19 11:25:54', '2025-12-19 11:25:58', '', '2025-12-19 11:04:32', '2025-12-19 11:25:58');
INSERT INTO `appointment` VALUES (5, '2001851979445956608', 2, 3, 1, '2025-12-24', '14:00-17:00', 2, 3.00, 4, NULL, '2025-12-19 11:25:52', '2025-12-19 11:25:55', '', '2025-12-19 11:07:59', '2025-12-19 11:25:55');
INSERT INTO `appointment` VALUES (6, '2001858046196260864', 2, 3, 2, '2026-01-09', '14:00-17:00', 2, 3.00, 4, NULL, '2025-12-19 11:33:01', '2025-12-19 11:33:39', '', '2025-12-19 11:32:05', '2025-12-19 11:33:38');
INSERT INTO `appointment` VALUES (7, '2001858808188051456', 2, 3, 1, '2025-12-25', '09:00-12:00', 1, 3.00, 4, NULL, '2025-12-20 10:23:23', '2025-12-24 16:48:00', '', '2025-12-19 11:35:07', '2025-12-24 16:47:59');
INSERT INTO `appointment` VALUES (8, '2001858864693714944', 2, 3, 2, '2027-12-02', '09:00-12:00', 1, 3.00, 4, NULL, '2025-12-20 10:23:20', '2025-12-20 10:31:12', '', '2025-12-19 11:35:20', '2025-12-20 10:31:12');
INSERT INTO `appointment` VALUES (9, '2002205328179556352', 6, 1, 1, '2025-12-22', '14:00-17:00', 1, 10000.00, 5, '门诊休息', NULL, NULL, '我要健康', '2025-12-20 10:32:04', '2025-12-20 10:34:20');
INSERT INTO `appointment` VALUES (10, '2003760107422748672', 2, 4, 1, '2025-12-31', '14:00-17:00', 2, 599.00, 4, NULL, '2025-12-24 17:30:56', '2025-12-24 17:36:41', '', '2025-12-24 17:30:12', '2025-12-24 17:36:41');
INSERT INTO `appointment` VALUES (11, '2003761898050920448', 2, 4, 1, '2025-12-25', '09:00-12:00', 1, 599.00, 4, NULL, '2025-12-24 17:37:52', '2025-12-24 17:38:28', '', '2025-12-24 17:37:19', '2025-12-24 17:38:27');
INSERT INTO `appointment` VALUES (12, '2007457069192040448', 2, 4, 1, '2026-01-21', '14:00-17:00', 1, 599.00, 1, NULL, NULL, NULL, '', '2026-01-03 22:20:36', '2026-01-03 22:30:22');
INSERT INTO `appointment` VALUES (13, '2007715818573967360', 12, 5, 17, '2026-01-04', '09:00-12:00', 1, 0.00, 4, NULL, '2026-01-04 23:30:06', '2026-01-04 23:31:40', '', '2026-01-04 15:28:47', '2026-01-04 23:31:40');
INSERT INTO `appointment` VALUES (14, '2007836741734064128', 12, 6, 21, '2026-01-05', '14:00-17:00', 1, 0.00, 4, NULL, '2026-01-04 23:30:02', '2026-01-04 23:30:17', '', '2026-01-04 23:29:17', '2026-01-04 23:30:16');
INSERT INTO `appointment` VALUES (15, '2008018103665479680', 12, 5, 5, '2026-01-06', '09:00-12:00', 2, 0.00, 4, NULL, '2026-01-05 12:06:07', '2026-02-27 16:39:05', '', '2026-01-05 11:29:57', '2026-02-27 16:39:05');
INSERT INTO `appointment` VALUES (16, '2011385299154337792', 12, 12, 4, '2026-01-29', '14:00-17:00', 3, 1298.00, 1, NULL, NULL, NULL, '', '2026-01-14 18:29:59', '2026-02-27 16:39:06');
INSERT INTO `appointment` VALUES (17, '2016907969438543872', 12, 5, 5, '2026-01-31', '09:00-12:00', 1, 0.00, 4, NULL, '2026-02-27 16:38:49', '2026-02-27 16:39:09', '', '2026-01-30 00:15:06', '2026-02-27 16:39:08');
INSERT INTO `appointment` VALUES (18, '2017242742908354560', 12, 9, 21, '2026-01-31', '14:00-17:00', 1, 128.00, 4, NULL, '2026-01-30 22:26:15', '2026-01-30 22:27:12', '', '2026-01-30 22:25:23', '2026-01-30 22:27:11');
INSERT INTO `appointment` VALUES (19, '2028046032873054208', 12, 9, 3, '2026-03-04', '09:00-12:00', 1, 128.00, 5, NULL, NULL, NULL, '', '2026-03-01 17:53:48', '2026-03-01 18:05:07');
INSERT INTO `appointment` VALUES (20, '2028047663203213312', 12, 9, 4, '2026-03-03', '09:00-12:00', 1, 128.00, 5, NULL, NULL, NULL, '', '2026-03-01 18:00:16', '2026-03-01 18:00:25');
INSERT INTO `appointment` VALUES (21, '2028047895609597952', 12, 9, 4, '2026-03-03', '09:00-12:00', 1, 128.00, 0, NULL, NULL, NULL, '', '2026-03-01 18:01:12', '2026-03-01 18:01:12');
INSERT INTO `appointment` VALUES (22, '2028050773808111616', 12, 5, 13, '2026-03-03', '09:00-12:00', 1, 0.00, 4, NULL, '2026-03-01 18:19:48', '2026-03-11 08:35:22', '', '2026-03-01 18:12:38', '2026-03-11 08:35:22');
INSERT INTO `appointment` VALUES (23, '2028051013550333952', 13, 5, 13, '2026-03-03', '09:00-12:00', 1, 0.00, 0, NULL, NULL, NULL, '', '2026-03-01 18:13:35', '2026-03-01 18:13:35');
INSERT INTO `appointment` VALUES (24, '2028051951287656448', 12, 5, 13, '2026-03-03', '09:00-12:00', 1, 0.00, 5, NULL, NULL, NULL, '', '2026-03-01 18:17:19', '2026-03-01 18:17:27');
INSERT INTO `appointment` VALUES (25, '2028052526448369664', 12, 7, 3, '2026-03-03', '09:00-12:00', 1, 0.00, 5, NULL, NULL, NULL, '', '2026-03-01 18:19:36', '2026-03-01 18:19:51');
INSERT INTO `appointment` VALUES (26, '2028052651493154816', 12, 13, 3, '2026-03-03', '09:00-12:00', 1, 798.00, 4, NULL, '2026-03-10 16:27:51', '2026-03-10 16:28:19', '', '2026-03-01 18:20:06', '2026-03-10 16:28:18');
INSERT INTO `appointment` VALUES (27, '2028054458378346496', 12, 5, 13, '2026-03-03', '09:00-12:00', 1, 0.00, 4, NULL, '2026-03-10 16:27:49', '2026-03-10 16:28:11', '', '2026-03-01 18:27:16', '2026-03-10 16:28:11');
INSERT INTO `appointment` VALUES (28, '2031220642330193920', 12, 9, 2, '2026-03-10', '09:00-12:00', 1, 128.00, 4, NULL, '2026-03-10 16:27:48', '2026-03-10 16:28:09', '', '2026-03-10 12:08:34', '2026-03-10 16:28:09');
INSERT INTO `appointment` VALUES (29, '2031220782927458304', 12, 12, 2, '2026-03-10', '09:00-12:00', 1, 1298.00, 4, NULL, '2026-03-10 16:27:46', '2026-03-10 16:28:08', '', '2026-03-10 12:09:07', '2026-03-10 16:28:07');
INSERT INTO `appointment` VALUES (30, '2031220850095042560', 12, 13, 2, '2026-03-10', '09:00-12:00', 1, 798.00, 4, NULL, '2026-03-10 16:27:45', '2026-03-10 16:28:06', '', '2026-03-10 12:09:23', '2026-03-10 16:28:05');
INSERT INTO `appointment` VALUES (31, '2031288301017223168', 12, 5, 13, '2026-03-10', '09:00-12:00', 1, 0.00, 1, NULL, NULL, NULL, '', '2026-03-10 16:37:25', '2026-03-10 17:46:33');
INSERT INTO `appointment` VALUES (32, '2031288381891792896', 12, 9, 3, '2026-03-11', '09:00-12:00', 1, 128.00, 1, NULL, NULL, NULL, '', '2026-03-10 16:37:44', '2026-03-10 17:46:32');
INSERT INTO `appointment` VALUES (33, '2031288578030030848', 15, 9, 3, '2026-03-11', '09:00-12:00', 1, 128.00, 1, NULL, NULL, NULL, '', '2026-03-10 16:38:31', '2026-03-10 17:46:31');
INSERT INTO `appointment` VALUES (34, '2031290179104919552', 15, 9, 3, '2026-03-11', '09:00-12:00', 1, 128.00, 1, NULL, NULL, NULL, '', '2026-03-10 16:44:52', '2026-03-10 17:46:31');
INSERT INTO `appointment` VALUES (35, '2031290543023603712', 15, 8, 3, '2026-03-11', '09:00-12:00', 1, 0.00, 1, NULL, NULL, NULL, '', '2026-03-10 16:46:19', '2026-03-10 17:46:30');
INSERT INTO `appointment` VALUES (36, '2031291629201539072', 15, 6, 3, '2026-03-11', '09:00-12:00', 1, 0.00, 1, NULL, NULL, NULL, '', '2026-03-10 16:50:38', '2026-03-10 17:46:29');
INSERT INTO `appointment` VALUES (37, '2031291758725840896', 15, 6, 4, '2026-03-11', '09:00-12:00', 1, 0.00, 1, NULL, NULL, NULL, '', '2026-03-10 16:51:09', '2026-03-10 17:46:28');
INSERT INTO `appointment` VALUES (38, '2031297540313858048', 15, 6, 3, '2026-03-11', '09:00-12:00', 1, 0.00, 5, NULL, NULL, NULL, '', '2026-03-10 17:14:07', '2026-03-10 17:16:08');
INSERT INTO `appointment` VALUES (39, '2031298303840763904', 15, 12, 1, '2026-03-26', '09:00-12:00', 1, 1298.00, 5, NULL, NULL, NULL, '', '2026-03-10 17:17:09', '2026-03-10 17:18:26');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片地址',
  `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '疫苗在线预约', '/uploads/2026/01/03/efe57b98254344d991427d99b1a5d4d8.jpg', '', 0, 1, '2025-12-17 23:47:14', '2026-01-03 22:25:01');
INSERT INTO `banner` VALUES (2, '关注健康', '/uploads/2026/01/03/21c4909352984e1aa58b9ff54bc83a10.png', '', 2, 1, '2025-12-17 23:47:31', '2026-01-03 22:28:01');
INSERT INTO `banner` VALUES (3, '全民接种疫苗', '/uploads/2025/12/24/060ca2bef4ed4620b830b3de501488dd.png', '', 1, 1, '2025-12-24 17:24:44', '2025-12-24 17:24:44');

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `vaccine_id` bigint NOT NULL COMMENT '疫苗ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_vaccine`(`user_id` ASC, `vaccine_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of collection
-- ----------------------------
INSERT INTO `collection` VALUES (2, 3, 3, '2025-12-17 23:39:09');
INSERT INTO `collection` VALUES (3, 2, 3, '2025-12-19 11:08:02');
INSERT INTO `collection` VALUES (4, 6, 3, '2025-12-20 10:36:57');
INSERT INTO `collection` VALUES (5, 6, 2, '2025-12-20 10:37:01');
INSERT INTO `collection` VALUES (6, 6, 1, '2025-12-20 10:37:06');
INSERT INTO `collection` VALUES (7, 12, 10, '2026-03-07 01:18:07');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `news_id` bigint NOT NULL COMMENT '资讯ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID（用于回复）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-待审核，1-已发布，2-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_news_id`(`news_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 2, '这个疫苗很哈', NULL, 1, '2025-12-19 21:14:07');
INSERT INTO `comment` VALUES (2, 1, 6, '很好', NULL, 1, '2025-12-19 21:18:11');
INSERT INTO `comment` VALUES (3, 7, 2, '好的', NULL, 1, '2026-01-03 23:34:11');
INSERT INTO `comment` VALUES (4, 2, 12, '很好', NULL, 1, '2026-01-04 22:34:03');
INSERT INTO `comment` VALUES (5, 2, 2, '确实好', NULL, 1, '2026-01-04 22:34:43');
INSERT INTO `comment` VALUES (6, 2, 2, '这个效果真的特别好', 5, 2, '2026-01-04 22:44:35');
INSERT INTO `comment` VALUES (7, 2, 2, '真的吗', 4, 1, '2026-01-04 22:44:50');
INSERT INTO `comment` VALUES (8, 2, 2, 'good', 5, 1, '2026-01-04 22:52:07');
INSERT INTO `comment` VALUES (9, 7, 12, '哈哈哈哈', 3, 1, '2026-01-04 23:32:42');
INSERT INTO `comment` VALUES (10, 3, 12, '很好', NULL, 1, '2026-03-10 16:14:14');
INSERT INTO `comment` VALUES (11, 3, 12, '这样子', 10, 1, '2026-03-10 16:14:20');

-- ----------------------------
-- Table structure for community
-- ----------------------------
DROP TABLE IF EXISTS `community`;
CREATE TABLE `community`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '社区ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '社区名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '社区地址',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '社区描述',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '社区图片',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '社区信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community
-- ----------------------------
INSERT INTO `community` VALUES (1, '迪迦社区', '迪迦', '111111111', '111', NULL, 1, '2025-12-17 17:18:58', '2025-12-17 17:18:58');
INSERT INTO `community` VALUES (2, '盖亚', '水口', '1111', '<h3><strong>我喜欢你哈哈哈哈饭卡法律开发了放啊管理fnkskfsankgagaljw1f]</strong></h3><h3><strong>骄傲计算方式发送反复看法i奉劝你卡ZK看</strong></h3>', '/uploads/2025/12/17/6b6920fe2795430280fa14bc85de381c.jpg', 1, '2025-12-17 17:39:20', '2025-12-18 23:59:35');
INSERT INTO `community` VALUES (3, '阳光花园社区', '北京市朝阳区阳光花园小区1号楼', '010-88881001', '<p>阳光花园社区成立于2008年，是朝阳区重点示范社区。社区配备专业医疗服务站，拥有标准化疫苗接种室，环境整洁舒适。</p><p>服务时间：周一至周五 8:30-17:00，周六 9:00-12:00</p>', '/uploads/2026/01/04/7c37f0dc055249abbe36154e26d07e5c.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:03:27');
INSERT INTO `community` VALUES (4, '翠苑街道社区', '北京市海淀区翠苑路88号', '010-88882002', '<p>翠苑街道社区卫生服务中心，承担辖区居民基本公共卫生服务。设有预防接种门诊，配备冷链设备齐全，接种环境规范。</p><p>特色服务：儿童预防接种、成人疫苗接种、健康咨询</p>', '/uploads/2026/01/04/27deb2799d6342dd8f0e5dd31ea49c08.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:03:34');
INSERT INTO `community` VALUES (5, '和平里社区', '北京市东城区和平里北街14号', '010-88883003', '<p>和平里社区卫生服务站是东城区优秀社区卫生服务机构，为居民提供便捷的疫苗接种服务。</p><p>接种须知：请携带身份证、接种本，提前预约。</p>', '/uploads/2026/01/04/cbe8e02feb3c42e591432afc16b75806.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:03:51');
INSERT INTO `community` VALUES (6, '望京西园社区', '北京市朝阳区望京西园四区423号', '010-88884004', '<p>望京西园社区服务中心位于望京核心区域，交通便利，服务周边10万余居民。</p><p>设施完善，拥有独立的预防接种区域，配备留观室。</p>', '/uploads/2026/01/04/82137ea7d6a440cf95c85685af0b3686.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:09:09');
INSERT INTO `community` VALUES (13, '双井社区', '北京市朝阳区双井桥东200米', '010-88880011', '<p>双井社区卫生服务站，服务双井、劲松地区居民，交通便利，地铁直达。</p>', '/uploads/2026/01/04/b23173bba1dc401bb687ec4ad5500d75.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:09:16');
INSERT INTO `community` VALUES (16, '上地社区', '北京市海淀区上地信息路28号', '010-88880014', '<p>上地社区卫生服务中心位于上地软件园区，主要服务IT从业人员。</p><p>提供流感疫苗团体接种服务。</p>', '/uploads/2026/01/04/ededa32fb79e410ea27f997d5d33548c.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:09:28');
INSERT INTO `community` VALUES (17, '清河社区', '北京市海淀区清河小营西路48号', '010-88880015', '<p>清河社区卫生服务中心，设施完善，服务规范，是海淀区北部重要的社区医疗机构。</p>', '/uploads/2026/01/04/6a4e108eaff5424296c7b4cb6474a275.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:09:38');
INSERT INTO `community` VALUES (19, '大兴黄村社区', '北京市大兴区黄村镇兴华大街', '010-88880017', '<p>大兴黄村社区卫生服务中心，大兴区规模最大的社区医疗机构之一。</p>', '/uploads/2026/01/04/b851cd586f114cba93be50a38681f4a7.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:09:46');
INSERT INTO `community` VALUES (20, '丰台科技园社区', '北京市丰台区科技园区丰科路', '010-88880018', '<p>丰台科技园社区卫生服务站，服务丰台科技园区企业和居民。</p>', '/uploads/2026/01/04/457bc315031c47959d0d6b50c5bf0b77.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:09:53');
INSERT INTO `community` VALUES (21, '石景山古城社区', '北京市石景山区古城大街', '010-88880019', '<p>石景山古城社区卫生服务中心，石景山区老牌社区医疗机构，口碑良好。</p>', '/uploads/2026/01/04/e2f9b5d1fe694a1ca182208b855ea594.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:10:02');
INSERT INTO `community` VALUES (22, '顺义后沙峪社区', '北京市顺义区后沙峪镇', '010-88880020', '<p>顺义后沙峪社区卫生服务站，服务后沙峪及周边别墅区居民。</p>', '/uploads/2026/01/04/2903801b8a534a99988ebf137a92d36a.png', 1, '2026-01-03 23:12:41', '2026-01-04 16:10:46');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '简介（富文本）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容（富文本）',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图',
  `community_id` bigint NULL DEFAULT NULL COMMENT '所属社区',
  `author_id` bigint NULL DEFAULT NULL COMMENT '作者ID',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已发布，2-已拒绝',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_community_id`(`community_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资讯信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (2, '2025年秋冬季流感疫苗接种指南', '<p>秋冬季是流感高发季节，建议老年人、儿童、慢性病患者等重点人群尽早接种流感疫苗。</p>', '<h3><span style=\"color: rgb(225, 60, 57);\"><strong>为什么要接种流感疫苗？</strong></span></h3><p>流感是由流感病毒引起的急性呼吸道传染病，每年秋冬季高发。接种流感疫苗是预防流感最有效的手段。</p><h3><span style=\"color: rgb(225, 60, 57);\"><strong>哪些人群应该优先接种？</strong></span></h3><ul><li>60岁及以上老年人</li><li>6月龄-5岁儿童</li><li>慢性病患者</li><li>医务人员</li><li>孕妇</li></ul><h3><span style=\"color: rgb(225, 60, 57);\"><strong>接种时间</strong></span></h3><p>建议在流感季节来临前（9-11月）完成接种，接种后2-4周产生保护性抗体。</p><h3><span style=\"color: rgb(225, 60, 57);\"><strong>注意事项</strong></span></h3><p>接种前请告知医生您的健康状况，接种后留观30分钟。</p>', '/uploads/2026/01/04/cbd76fbad0414276b6bbd0ea6dd50d51.png', 1, 1, 1274, 1, '2026-01-03 23:33:26', '2026-01-04 23:38:09');
INSERT INTO `news` VALUES (3, 'HPV疫苗知识科普：九价、四价、二价如何选择？', '<p>HPV疫苗是预防宫颈癌的有效手段，本文为您详细解读不同价型HPV疫苗的区别和选择建议。</p>', '<h3>什么是HPV？</h3><p>HPV（人乳头瘤病毒）是一种常见的性传播病毒，高危型HPV持续感染是导致宫颈癌的主要原因。</p><h3>三种HPV疫苗的区别</h3><p><strong>二价疫苗：</strong>预防HPV16、18型，可预防约70%的宫颈癌。</p><p><strong>四价疫苗：</strong>预防HPV6、11、16、18型，除宫颈癌外还可预防尖锐湿疣。</p><p><strong>九价疫苗：</strong>预防9种HPV型别，可预防约90%的宫颈癌。</p><h3>如何选择？</h3><p>建议根据年龄、预算和疫苗供应情况综合考虑。无论选择哪种，尽早接种都能获得更好的保护效果。</p>', '/uploads/2026/01/04/a8b1e33a951d44d78710af02912da73e.png', 2, 1, 2359, 1, '2026-01-03 23:33:26', '2026-03-11 00:47:32');
INSERT INTO `news` VALUES (5, '儿童疫苗接种时间表，家长必看！', '<p>儿童免疫规划疫苗接种时间表，帮助家长了解孩子各年龄段需要接种的疫苗。</p>', '<h3>出生时</h3><p>乙肝疫苗第1剂、卡介苗</p><h3>1月龄</h3><p>乙肝疫苗第2剂</p><h3>2月龄</h3><p>脊灰疫苗第1剂</p><h3>3月龄</h3><p>脊灰疫苗第2剂、百白破疫苗第1剂</p><h3>4月龄</h3><p>脊灰疫苗第3剂、百白破疫苗第2剂</p><h3>5月龄</h3><p>百白破疫苗第3剂</p><h3>6月龄</h3><p>乙肝疫苗第3剂、A群流脑疫苗第1剂</p><h3>8月龄</h3><p>麻腮风疫苗第1剂、乙脑减毒活疫苗第1剂</p><p><strong>温馨提示：</strong>请按时带孩子接种疫苗，如有特殊情况请咨询接种门诊。</p>', '/uploads/2026/01/04/460beb8c898042f0bdc671e859532e6d.jpg', 4, 1, 1701, 1, '2026-01-03 23:33:26', '2026-01-05 10:51:24');
INSERT INTO `news` VALUES (6, '疫苗接种后的注意事项', '<p>接种疫苗后需要注意什么？本文为您详细介绍接种后的护理要点和常见问题解答。</p>', '<h3>接种后留观</h3><p>接种后请在接种点留观30分钟，观察是否有异常反应。</p><h3>接种部位护理</h3><ul><li>保持接种部位清洁干燥</li><li>24小时内避免沾水</li><li>不要揉搓接种部位</li></ul><h3>常见反应及处理</h3><p><strong>局部反应：</strong>接种部位红肿、硬结，一般2-3天自行消退。</p><p><strong>全身反应：</strong>低热、乏力，多喝水、注意休息即可。</p><h3>何时需要就医？</h3><ul><li>高热持续不退（超过38.5℃）</li><li>接种部位严重红肿</li><li>出现皮疹、呼吸困难等过敏症状</li></ul>', '/uploads/2026/01/04/599c1dcfa9744e7e9e050596c505f48f.png', 5, 1, 1123, 1, '2026-01-03 23:33:26', '2026-01-05 12:06:54');
INSERT INTO `news` VALUES (7, '老年人疫苗接种建议', '<p>老年人免疫力较低，更需要通过接种疫苗来预防疾病。本文介绍适合老年人接种的疫苗。</p>', '<h3>推荐接种的疫苗</h3><p><strong>流感疫苗：</strong>每年接种，预防季节性流感。</p><p><strong>肺炎疫苗：</strong>23价肺炎球菌多糖疫苗，预防肺炎球菌感染。</p><p><strong>带状疱疹疫苗：</strong>50岁以上人群可接种，预防带状疱疹。</p><h3>接种注意事项</h3><ul><li>接种前告知医生既往病史和用药情况</li><li>慢性病患者在病情稳定期接种</li><li>接种后注意休息，多饮水</li></ul>', '/uploads/2026/01/04/ccc324eacbf949119150e6d17aadcb59.png', 6, 1, 783, 1, '2026-01-03 23:33:26', '2026-01-04 23:32:35');
INSERT INTO `news` VALUES (8, '孕妇可以接种疫苗吗？', '<p>很多准妈妈关心孕期能否接种疫苗，本文为您解答孕期疫苗接种的相关问题。</p>', '<h3>孕期可以接种的疫苗</h3><p><strong>流感疫苗：</strong>孕期任何阶段都可以接种，保护母婴健康。</p><p><strong>百白破疫苗：</strong>孕晚期（27-36周）接种，保护新生儿免受百日咳侵害。</p><h3>孕期禁止接种的疫苗</h3><p>减毒活疫苗（如麻腮风、水痘疫苗）孕期禁止接种。</p><h3>备孕期注意事项</h3><p>建议在怀孕前完成HPV疫苗、麻腮风疫苗等接种，接种后1-3个月再备孕。</p>', '/uploads/2026/01/04/c98afcfc786f43a1a507b2f1c0f8c6cb.png', 7, 1, 653, 1, '2026-01-03 23:33:26', '2026-01-04 22:28:23');
INSERT INTO `news` VALUES (9, '狂犬病预防知识：被动物咬伤后怎么办？', '<p>被猫狗等动物咬伤后，正确的处理方式可以有效预防狂犬病。</p>', '<h3>伤口处理</h3><p>立即用肥皂水和流动清水交替冲洗伤口至少15分钟，然后用碘伏消毒。</p><h3>及时就医</h3><p>尽快到医院或疾控中心进行伤口处理和狂犬病暴露评估。</p><h3>疫苗接种</h3><p>根据暴露等级，医生会制定相应的疫苗接种方案：</p><ul><li>II级暴露：接种狂犬病疫苗</li><li>III级暴露：接种狂犬病疫苗+狂犬病免疫球蛋白</li></ul><h3>重要提醒</h3><p>狂犬病一旦发病，病死率几乎100%，请务必重视暴露后的预防处理！</p>', '/uploads/2026/01/04/3108c24c65ac4a3c9012ab481e629f2c.png', 8, 1, 921, 1, '2026-01-03 23:33:26', '2026-01-04 22:28:35');
INSERT INTO `news` VALUES (10, '水痘疫苗：预防水痘和带状疱疹', '<p>水痘疫苗不仅能预防儿童水痘，还能降低成年后患带状疱疹的风险。</p>', '<h3>什么是水痘？</h3><p>水痘是由水痘-带状疱疹病毒引起的急性传染病，主要通过呼吸道和接触传播。</p><h3>接种对象</h3><p>1岁及以上未患过水痘的人群均可接种。</p><h3>接种程序</h3><p>推荐接种2剂，第1剂在12-15月龄，第2剂在4-6岁。</p><h3>接种效果</h3><p>接种2剂水痘疫苗后，保护效果可达98%以上。</p>', '/uploads/2026/01/04/900fcefe8afd4d8f874b45171d2ae7be.png', 9, 1, 541, 1, '2026-01-03 23:33:26', '2026-01-04 22:28:46');
INSERT INTO `news` VALUES (11, '乙肝疫苗补种指南', '<p>错过乙肝疫苗接种怎么办？本文介绍乙肝疫苗补种的相关知识。</p>', '<h3>谁需要补种？</h3><ul><li>未接种过乙肝疫苗的成人</li><li>接种后抗体阴性者</li><li>乙肝高危人群</li></ul><h3>补种程序</h3><p>按0-1-6月程序接种3剂。</p><h3>接种后检测</h3><p>完成全程接种后1-2个月，建议检测乙肝表面抗体，确认是否产生保护性抗体。</p><h3>抗体阴性怎么办？</h3><p>可加强接种1-3剂，或更换其他厂家疫苗重新接种。</p>', '/uploads/2026/01/04/14921c09dc0649ceb12bceb51962a4dd.png', 10, 1, 481, 1, '2026-01-03 23:33:26', '2026-01-04 22:31:36');
INSERT INTO `news` VALUES (12, '社区疫苗接种服务升级通知', '<p>为提升居民接种体验，我社区卫生服务中心对预防接种门诊进行了全面升级改造。</p>', '<h3>升级内容</h3><ul><li>新增智能预约系统，支持线上预约</li><li>扩大接种区域，增设留观室</li><li>配备冷链监控系统，保障疫苗质量</li><li>增加接种时段，方便上班族</li></ul><h3>新增服务时间</h3><p>周一至周五延时服务至19:00</p><p>周六全天服务 9:00-17:00</p><h3>温馨提示</h3><p>请通过本系统提前预约，避免现场排队等候。</p>', '/uploads/2026/01/04/09d4d378313e425b9ab0bd99db030c83.png', 1, 1, 320, 1, '2026-01-03 23:33:26', '2026-01-04 22:28:57');
INSERT INTO `news` VALUES (14, '疫苗冷链管理，保障接种安全', '<p>疫苗从生产到接种全程需要冷链保存，我们如何保障疫苗质量？</p>', '<h3>什么是疫苗冷链？</h3><p>疫苗冷链是指疫苗从生产、储存、运输到使用的全过程中，始终保持在规定的温度条件下。</p><h3>我们的保障措施</h3><ul><li>配备专业医用冷藏设备</li><li>24小时温度监控系统</li><li>定期设备维护和校准</li><li>严格的疫苗出入库管理</li></ul><h3>居民可以放心</h3><p>每支疫苗都有完整的追溯信息，可通过扫码查询疫苗来源和储存记录。</p>', '/uploads/2026/01/04/b9c30bb1df2a4d679627ce98e893c53b.png', 3, 1, 280, 1, '2026-01-03 23:33:26', '2026-01-04 22:32:10');
INSERT INTO `news` VALUES (15, '预防接种证的重要性', '<p>预防接种证是儿童入托、入学的必备证件，请家长妥善保管。</p>', '<h3>什么是预防接种证？</h3><p>预防接种证是记录儿童疫苗接种情况的重要凭证，由接种单位在首次接种时发放。</p><h3>接种证的用途</h3><ul><li>入托、入学查验</li><li>出国留学、移民</li><li>记录接种历史</li></ul><h3>遗失补办</h3><p>如接种证遗失，可携带户口本到原接种单位申请补办。</p><h3>温馨提示</h3><p>每次接种请携带接种证，接种后请核对记录是否正确。</p>', '/uploads/2026/01/04/165d3167bf8649e3b7b9c9831a336d5b.png', 4, 1, 350, 1, '2026-01-03 23:33:26', '2026-01-04 22:32:18');
INSERT INTO `news` VALUES (16, '成人也需要接种疫苗吗？', '<p>疫苗不只是儿童的专利，成人同样需要通过接种疫苗来预防疾病。</p>', '<h3>成人推荐接种的疫苗</h3><p><strong>流感疫苗：</strong>每年接种，所有成人均可接种。</p><p><strong>乙肝疫苗：</strong>未接种或抗体阴性者。</p><p><strong>HPV疫苗：</strong>9-45岁女性。</p><p><strong>带状疱疹疫苗：</strong>50岁以上人群。</p><p><strong>肺炎疫苗：</strong>60岁以上老年人、慢性病患者。</p><h3>接种建议</h3><p>建议成人定期评估自己的疫苗接种情况，及时补种或加强接种。</p>', '/uploads/2026/01/04/1bd3d150c03646f1a15e1994e0951736.png', 5, 1, 520, 1, '2026-01-03 23:33:26', '2026-01-04 22:32:29');

-- ----------------------------
-- Table structure for recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '充值金额',
  `before_balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值前余额',
  `after_balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值后余额',
  `pay_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recharge_record
-- ----------------------------
INSERT INTO `recharge_record` VALUES (1, 3, 10000.00, 0.00, 10000.00, '支付宝', 1, '2025-12-17 17:31:56');
INSERT INTO `recharge_record` VALUES (2, 2, 100.00, 0.00, 100.00, '支付宝', 1, '2025-12-17 17:33:39');
INSERT INTO `recharge_record` VALUES (3, 2, 10000.00, 100.00, 10100.00, '支付宝', 1, '2025-12-17 17:41:41');
INSERT INTO `recharge_record` VALUES (4, 2, 10000.00, 10100.00, 20100.00, '支付宝', 1, '2025-12-17 17:42:53');
INSERT INTO `recharge_record` VALUES (5, 2, 10000.00, 20100.00, 30100.00, '支付宝', 1, '2025-12-17 17:43:02');
INSERT INTO `recharge_record` VALUES (6, 4, 500.00, 0.00, 500.00, '微信', 1, '2025-12-19 10:41:26');
INSERT INTO `recharge_record` VALUES (7, 2, 100.00, 20091.00, 20191.00, '支付宝', 1, '2025-12-19 11:36:07');
INSERT INTO `recharge_record` VALUES (8, 2, 100.00, 20185.00, 20285.00, '支付宝', 1, '2025-12-20 10:25:01');
INSERT INTO `recharge_record` VALUES (9, 6, 100.00, 0.00, 100.00, '支付宝', 1, '2025-12-20 10:39:52');
INSERT INTO `recharge_record` VALUES (10, 6, 100.00, 100.00, 200.00, '微信', 1, '2025-12-20 10:40:01');
INSERT INTO `recharge_record` VALUES (11, 12, 100.00, 0.00, 100.00, '支付宝', 1, '2026-01-04 23:32:59');
INSERT INTO `recharge_record` VALUES (12, 12, 300.00, 100.00, 400.00, '支付宝', 1, '2026-01-30 22:26:10');
INSERT INTO `recharge_record` VALUES (13, 12, 10000.00, 272.00, 10272.00, '支付宝', 1, '2026-03-10 16:27:38');

-- ----------------------------
-- Table structure for time_slot_capacity
-- ----------------------------
DROP TABLE IF EXISTS `time_slot_capacity`;
CREATE TABLE `time_slot_capacity`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `community_id` bigint NOT NULL COMMENT '社区ID',
  `slot_date` date NOT NULL COMMENT '日期',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '时段',
  `capacity` int NULL DEFAULT 50 COMMENT '容量',
  `booked` int NULL DEFAULT 0 COMMENT '已预约数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_community_date_slot`(`community_id` ASC, `slot_date` ASC, `time_slot` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '预约时段容量表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of time_slot_capacity
-- ----------------------------
INSERT INTO `time_slot_capacity` VALUES (1, 3, '2026-03-11', '09:00-12:00', 50, 2, '2026-03-10 16:46:19');
INSERT INTO `time_slot_capacity` VALUES (2, 4, '2026-03-11', '09:00-12:00', 50, 1, '2026-03-10 16:51:09');
INSERT INTO `time_slot_capacity` VALUES (3, 1, '2026-03-26', '09:00-12:00', 50, 0, '2026-03-10 17:17:09');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `id_card` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号（加密存储）',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '普通用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 'a', '$2a$10$sokWknMj4U6G61gb.Ii0A.Fp48rpA5JK68oKOniKKCBFSNoB/okLq', 'hzc', 'q', 'q', 'q', '/uploads/2025/12/19/0076cce6552d48c5be047d91e597f9b1.jpg', 19087.00, 1, '2025-12-17 16:57:09', '2025-12-24 17:37:52');
INSERT INTO `user` VALUES (6, 'aa', '$2a$10$BAet6p5J.iJ0ee0Eo8ZgiO05bWR6PY9mn1RzIG4sAWyGIgHL2ulhm', 'a', 'a', NULL, NULL, '/uploads/2025/12/19/7d5ce273bf0a4481a831c9d091609105.jpg', 200.00, 1, '2025-12-19 21:17:58', '2025-12-20 10:40:01');
INSERT INTO `user` VALUES (12, 'hzx', '$2a$10$0ebbqbPXvdsdKGkgrA9sSOBehA10P23RhhU8D7Y29xJ6ngvKoKtMq', '黄志雄', '15113308609', NULL, NULL, NULL, 7250.00, 1, '2026-01-03 23:35:19', '2026-03-10 16:27:50');
INSERT INTO `user` VALUES (13, 'hzc', '$2a$10$/7bk2TCUIQYkdXqMJlEtLuDBPx3U2YWh7ESbe59oEAJhAOIJqwDei', '1', '1', NULL, NULL, NULL, 0.00, 0, '2026-03-01 18:13:20', '2026-03-10 16:19:16');
INSERT INTO `user` VALUES (14, '包租公', '$2a$10$6y/GnYyb877HHXW0emXDJecYVGKFw.I7YtpgcaB/eN9jk7ce4x.dO', '装好东', '11011011011', NULL, NULL, NULL, 0.00, 1, '2026-03-10 16:17:06', '2026-03-10 16:17:06');
INSERT INTO `user` VALUES (15, '111', '$2a$10$ZwYWTi/z58Tycdu9DstdeeM/BE5SBFHzBnDo4F1NZKAd3tTo5nY.2', 'q', 'q', NULL, NULL, NULL, 0.00, 1, '2026-03-10 16:38:18', '2026-03-10 16:38:18');

-- ----------------------------
-- Table structure for vaccine
-- ----------------------------
DROP TABLE IF EXISTS `vaccine`;
CREATE TABLE `vaccine`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '疫苗ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '疫苗名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `manufacturer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NULL DEFAULT 0 COMMENT '库存数量',
  `doses` int NULL DEFAULT 1 COMMENT '接种剂次',
  `interval_days` int NULL DEFAULT 0 COMMENT '接种间隔天数',
  `target_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '适用人群',
  `contraindication` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '禁忌症',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '疫苗描述（富文本）',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '疫苗图片',
  `appointment_count` int NULL DEFAULT 0 COMMENT '预约数量',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_appointment_count`(`appointment_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '疫苗信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vaccine
-- ----------------------------
INSERT INTO `vaccine` VALUES (5, '科兴新冠灭活疫苗', 6, '北京科兴中维生物技术有限公司', 0.00, 0, 2, 21, '18岁及以上人群', '对疫苗成分过敏者、患有急性疾病者禁用', '科兴新冠灭活疫苗采用灭活技术，安全性高，已获得世界卫生组织紧急使用认证。', '/uploads/2026/01/03/bcde8486942844a0a9456f199df0300d.png', 1287, 1, '2026-01-03 22:47:02', '2026-03-10 17:23:30');
INSERT INTO `vaccine` VALUES (6, '国药新冠灭活疫苗', 6, '国药集团中国生物', 0.00, 597, 2, 21, '18岁及以上人群', '严重过敏体质者禁用', '国药集团研发的新冠灭活疫苗，保护效力达79%以上。', '/uploads/2026/01/03/2e26e73a0376435399d5c75d2a3b88f3.png', 1563, 1, '2026-01-03 22:47:02', '2026-03-10 17:23:37');
INSERT INTO `vaccine` VALUES (7, '康希诺腺病毒载体疫苗', 1, '康希诺生物股份公司', 0.00, 300, 1, 0, '18岁及以上人群', '对腺病毒过敏者禁用', '单针接种，采用腺病毒载体技术，接种更便捷。', '/uploads/2026/01/03/5a14e211711d4e83a9807d8e39ddbdde.png', 890, 1, '2026-01-03 22:47:02', '2026-03-01 18:19:51');
INSERT INTO `vaccine` VALUES (8, '智飞重组蛋白疫苗', 1, '安徽智飞龙科马生物制药', 0.00, 399, 3, 28, '18岁及以上人群', '免疫功能缺陷者慎用', '重组蛋白技术路线，需接种3剂次，安全性良好。', '/uploads/2026/01/04/6841bd389d67495face4f3606ecc8630.png', 651, 1, '2026-01-03 22:47:02', '2026-03-10 16:46:19');
INSERT INTO `vaccine` VALUES (9, '四价流感病毒裂解疫苗', 7, '华兰生物疫苗股份有限公司', 128.00, 794, 1, 0, '3岁及以上人群', '对鸡蛋过敏者禁用', '覆盖4种流感病毒株，保护更全面，建议每年秋季接种。', '/uploads/2026/01/03/9c8fb80dca7b4298a2e3487d34579c16.png', 2346, 1, '2026-01-03 22:47:02', '2026-03-10 17:24:38');
INSERT INTO `vaccine` VALUES (10, '三价流感疫苗', 7, '长春生物制品研究所', 68.00, 1000, 1, 0, '6月龄及以上人群', '发热患者暂缓接种', '经典三价流感疫苗，性价比高，适合大众接种。', '/uploads/2026/01/04/f71b16398a054dca8c1fd2d5275cd6f5.png', 1890, 1, '2026-01-03 22:47:02', '2026-03-10 17:23:57');
INSERT INTO `vaccine` VALUES (11, '鼻喷流感减毒活疫苗', 2, '长春百克生物科技', 298.00, 200, 1, 0, '3-17岁人群', '免疫缺陷者禁用', '无需注射，鼻腔喷雾接种，儿童接受度高。', '/uploads/2026/01/03/6aeec8deff4b4e6881b785e67a18b020.png', 560, 1, '2026-01-03 22:47:02', '2026-01-03 23:04:22');
INSERT INTO `vaccine` VALUES (12, '九价HPV疫苗', 8, '默沙东（中国）', 1298.00, 148, 3, 60, '9-45岁女性', '孕妇禁用、对酵母过敏者禁用', '覆盖9种HPV型别，预防约90%的宫颈癌，是目前保护最全面的HPV疫苗。', '/uploads/2026/01/03/b415254e150945408de035626e36b643.png', 3202, 1, '2026-01-03 22:47:02', '2026-03-10 17:24:04');
INSERT INTO `vaccine` VALUES (13, '四价HPV疫苗', 8, '默沙东（中国）', 798.00, 298, 3, 60, '9-45岁女性', '孕妇禁用', '覆盖HPV6/11/16/18四种型别，可预防约70%的宫颈癌。', '/uploads/2026/01/03/72f6fec699ca4d86b1cc1bd95c6e66f3.png', 2102, 1, '2026-01-03 22:47:02', '2026-03-10 17:24:15');
INSERT INTO `vaccine` VALUES (14, '二价HPV疫苗', 8, '葛兰素史克', 580.00, 500, 3, 30, '9-45岁女性', '孕妇及哺乳期禁用', '针对HPV16/18型，预防约70%的宫颈癌，价格实惠。', '/uploads/2026/01/03/39dc8f93c6c949a7afd1674eba5aa298.png', 1650, 1, '2026-01-03 22:47:02', '2026-03-10 17:24:23');
INSERT INTO `vaccine` VALUES (16, '重组乙型肝炎疫苗(酵母)', 4, '深圳康泰生物制品', 25.00, 800, 3, 30, '新生儿及成人', '对酵母过敏者禁用', '采用基因重组技术，安全有效，是预防乙肝的首选疫苗。', '/uploads/2026/01/04/9e56dc93d1244d7281a2f5eedd45f472.png', 980, 1, '2026-01-03 22:47:02', '2026-01-04 15:55:01');
INSERT INTO `vaccine` VALUES (17, '重组乙型肝炎疫苗(CHO细胞)', 4, '华北制药金坦生物', 65.00, 400, 3, 30, '成人', '严重心脏病患者慎用', 'CHO细胞表达系统，免疫原性强，适合乙肝高危人群。', '/uploads/2026/01/04/6b052e6cdd094fc9868186352906c491.png', 560, 1, '2026-01-03 22:47:02', '2026-01-04 15:54:51');
INSERT INTO `vaccine` VALUES (18, '人用狂犬病疫苗(Vero细胞)', 5, '辽宁成大生物股份有限公司', 280.00, 300, 5, 3, '被动物咬伤者', '无绝对禁忌症', '暴露后预防用疫苗，需按0-3-7-14-28天程序接种5剂。', '/uploads/2026/01/04/4649ec40a5494753a8fc94591c95819c.png', 450, 1, '2026-01-03 22:47:02', '2026-01-04 15:54:44');
INSERT INTO `vaccine` VALUES (23, '13价肺炎球菌多糖结合疫苗', 8, '辉瑞制药', 698.00, 300, 4, 30, '6周龄-15月龄婴幼儿', '对疫苗成分过敏者禁用', '覆盖13种肺炎球菌血清型，婴幼儿肺炎预防首选。', '/uploads/2026/01/04/e681761ad23e4a12a322fd773a167c71.png', 890, 1, '2026-01-03 22:47:02', '2026-01-04 15:54:36');
INSERT INTO `vaccine` VALUES (24, '23价肺炎球菌多糖疫苗', 8, '成都生物制品研究所', 198.00, 500, 1, 0, '2岁及以上人群', '脾切除者慎用', '覆盖23种血清型，适合老年人和慢性病患者。', '/uploads/2026/01/04/aeba69cc9b8645a79ffec2a485abcbd5.png', 670, 1, '2026-01-03 22:47:02', '2026-01-04 15:54:28');
INSERT INTO `vaccine` VALUES (25, '甲型肝炎灭活疫苗', 9, '北京科兴生物制品', 98.00, 400, 2, 180, '1岁及以上人群', '急性发热者暂缓', '灭活疫苗，安全性高，需接种2剂。', '/uploads/2026/01/04/6bacd53e80f34d989f90ccc4c358a9bc.png', 520, 1, '2026-01-03 22:47:02', '2026-01-04 15:54:19');
INSERT INTO `vaccine` VALUES (26, '甲型肝炎减毒活疫苗', 9, '长春生物制品研究所', 0.00, 600, 1, 0, '18月龄及以上', '免疫缺陷者禁用', '国家免疫规划疫苗，单剂接种，免费。', '/uploads/2026/01/04/f305bf97fce14217947d2643105bf7f0.png', 890, 1, '2026-01-03 22:47:02', '2026-01-04 15:54:10');
INSERT INTO `vaccine` VALUES (27, 'A群C群脑膜炎球菌多糖疫苗', 10, '武汉生物制品研究所', 0.00, 500, 2, 90, '2岁及以上人群', '癫痫患者禁用', '预防A群和C群脑膜炎球菌引起的流行性脑脊髓膜炎。', '/uploads/2026/01/04/ca2660e88617474387710a7c358a48f6.png', 680, 1, '2026-01-03 22:47:02', '2026-01-04 15:53:57');
INSERT INTO `vaccine` VALUES (28, 'ACYW135群脑膜炎球菌多糖疫苗', 10, '华兰生物', 168.00, 300, 1, 0, '2岁及以上人群', '神经系统疾病者慎用', '四价脑膜炎疫苗，保护更全面，出国留学推荐接种。', '/uploads/2026/01/04/ec565f3ec0f34ca89573ccc9f8ea8f89.png', 420, 1, '2026-01-03 22:47:02', '2026-01-04 15:45:44');
INSERT INTO `vaccine` VALUES (29, '吸附无细胞百白破联合疫苗', 11, '武汉生物制品研究所', 0.00, 800, 4, 30, '3月龄-6岁儿童', '神经系统疾病者禁用', '国家免疫规划疫苗，预防百日咳、白喉、破伤风。', '/uploads/2026/01/04/e4ba3010b4df49dab8048148697039ef.png', 2100, 1, '2026-01-03 22:47:02', '2026-01-04 15:45:36');
INSERT INTO `vaccine` VALUES (30, '口服轮状病毒活疫苗', 12, '兰州生物制品研究所', 168.00, 400, 3, 30, '2月龄-3岁婴幼儿', '免疫缺陷者禁用', '口服接种，预防婴幼儿轮状病毒腹泻，建议每年接种。', '/uploads/2026/01/04/d9c7049b3bc94b468fe3f73a11b3ef53.png', 560, 1, '2026-01-03 22:47:02', '2026-01-04 15:45:30');
INSERT INTO `vaccine` VALUES (31, '五价轮状病毒疫苗', 12, '默沙东（中国）', 258.00, 200, 3, 30, '6周龄-32周龄婴儿', '肠套叠病史者禁用', '覆盖5种轮状病毒型别，保护效果更好。', '/uploads/2026/01/04/acad14c38dbb4345ba2f9c166b4f1645.png', 380, 1, '2026-01-03 22:47:02', '2026-01-04 15:45:22');

-- ----------------------------
-- Table structure for vaccine_category
-- ----------------------------
DROP TABLE IF EXISTS `vaccine_category`;
CREATE TABLE `vaccine_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '疫苗分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vaccine_category
-- ----------------------------
INSERT INTO `vaccine_category` VALUES (6, '新冠疫苗', '新型冠状病毒疫苗，用于预防COVID-19感染', 1, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (7, '流感疫苗', '季节性流感疫苗，建议每年接种', 2, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (8, 'HPV疫苗', '人乳头瘤病毒疫苗，预防宫颈癌等疾病', 3, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (9, '乙肝疫苗', '乙型肝炎疫苗，预防乙型肝炎病毒感染', 4, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (10, '狂犬疫苗', '狂犬病疫苗，被动物咬伤后接种', 5, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (11, '水痘疫苗', '水痘-带状疱疹病毒疫苗', 6, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (12, '麻腮风疫苗', '麻疹、腮腺炎、风疹联合疫苗', 7, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (13, '肺炎疫苗', '肺炎球菌疫苗，预防肺炎球菌感染', 8, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (14, '甲肝疫苗', '甲型肝炎疫苗', 9, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (15, '脑膜炎疫苗', '流行性脑脊髓膜炎疫苗', 10, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (16, '百白破疫苗', '百日咳、白喉、破伤风联合疫苗', 11, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');
INSERT INTO `vaccine_category` VALUES (17, '轮状病毒疫苗', '预防婴幼儿轮状病毒腹泻', 12, 1, '2026-01-03 22:47:02', '2026-01-03 22:47:02');

SET FOREIGN_KEY_CHECKS = 1;
