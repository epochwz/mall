#!/bin/bash

MALL_PATH=$HOME/srccode/mall
MALL_REPO=https://github.com/epochwz/mall.git
MALL_CONF_SRC=$MALL_PATH/prod/resources.pro
MALL_CONF_DES=$MALL_PATH/src/main

# 克隆源代码
[ ! -d "$HOME/srccode/mall" ] && git clone $MALL_REPO $MALL_PATH

# 复制生产环境配置文件
[ ! -d "$MALL_CONF_SRC" ] && mkdir -p $MALL_CONF_SRC && cp -r $MALL_PATH/demo/resources.pro/* $MALL_CONF_SRC

# 更新生产环境配置文件
cp -r $MALL_CONF_SRC $MALL_CONF_DES

# 进入源代码目录、更新源代码
cd $MALL_PATH && git pull

# 打包
mvn clean package -Dmaven.test.skip=true -Ppro -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
# 清除
rm -rf $CATALINA_HOME/webapps/mall*
rm -rf $CATALINA_HOME/work/Catalina/localhost/mall
# 拷贝
mv $MALL_PATH/target/mall.war $CATALINA_HOME/webapps/