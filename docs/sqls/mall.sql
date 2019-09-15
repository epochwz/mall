DROP DATABASE IF EXISTS `mall`;

CREATE DATABASE `mall` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
# collation: 指定字符串排序规则和比较规则
#
# utf8_bin：二进制存储字符串，区分大小写
# utf8_general_cs：大小写敏感
# utf8_general_ci：大小写不敏感

USE `mall`;

SET NAMES utf8;
# SET NAMES utf8;
# equal to
# SET character_set_client='utf8';
# SET character_set_connection='utf8';
# SET character_set_results='utf8';

SET FOREIGN_KEY_CHECKS = 0;

SET FOREIGN_KEY_CHECKS = 1;