/*
Navicat MySQL Data Transfer

Source Server         : linux mysql
Source Server Version : 50173
Source Host           : 192.168.91.131:3306
Source Database       : ftpdemo

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2018-03-06 14:55:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(32) NOT NULL,
  `code` varchar(10) NOT NULL COMMENT '员工编号',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `realname` varchar(20) NOT NULL COMMENT '姓名',
  `age` int(2) NOT NULL COMMENT '年龄',
  `empdate` date NOT NULL COMMENT '入职日期',
  `address` varchar(50) NOT NULL COMMENT '家庭地址'
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='用户';
