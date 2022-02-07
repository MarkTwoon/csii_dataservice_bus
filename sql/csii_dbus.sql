/*
 Navicat Premium Data Transfer

 Source Server         : mysqlDemo
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : localhost:3306
 Source Schema         : csii_dbus

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 07/02/2022 22:56:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for payment
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serial` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment
-- ----------------------------
INSERT INTO `payment` VALUES (1, '我是测试数据！');
INSERT INTO `payment` VALUES (2, '我是又一条测试数据22！');
INSERT INTO `payment` VALUES (3, '我是又一条测试数据33！');

SET FOREIGN_KEY_CHECKS = 1;
