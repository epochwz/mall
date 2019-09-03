DROP DATABASE IF EXISTS `mall`;

CREATE DATABASE `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# collation: 指定字符串排序规则和比较规则
#
# utf8mb4_unicode_ci: 基于标准的 Unicode 来排序和比较，能够在各种语言之间精确排序
# utf8mb4_general_ci: 没有实现 Unicode 排序规则，在遇到某些特殊语言或者字符集，排序结果可能不一致。
#
# utf8=utf8mb3
# utf8_bin：二进制存储字符串，区分大小写
# utf8_general_cs：大小写敏感
# utf8_general_ci：大小写不敏感


USE `mall`;

SET NAMES utf8mb4;
# SET NAMES utf8;
# equal to
# SET character_set_client='utf8';
# SET character_set_connection='utf8';
# SET character_set_results='utf8';

SET FOREIGN_KEY_CHECKS = 0;

SET FOREIGN_KEY_CHECKS = 1;