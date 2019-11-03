#!/bin/bash
# ============================== ⬇⬇⬇  无需改动 ⬇⬇⬇ ==============================
# 获取脚本名称
SELF_NAME=$(basename $BASH_SOURCE)
# 获取脚本路径
SELF_PATH=$(cd `dirname $0` && pwd)/$SELF_NAME
# ============================== ⬆⬆⬆  无需改动 ⬆⬆⬆ ==============================

export ENV_FILE=$HOME/.bash_aliases && touch $ENV_FILE      # 环境变量
export ZIP_PATH=$HOME/packages && mkdir -p $ZIP_PATH        # 下载路径
export SETUP_PATH=$HOME/softwares && mkdir -p $SETUP_PATH   # 软件路径
export SOURCE_PATH=$HOME/srccode  && mkdir -p $SOURCE_PATH  # 源码路径
export MALL_PATH=$SOURCE_PATH/mall                          # 项目路径

JDK(){
    # 下载和安装
    sudo apt-get update -y && sudo apt-get install openjdk-8-jdk -y

    # 配置环境变量
    echo "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/" >> $ENV_FILE
    # 重载环境变量
    source $ENV_FILE

    # 验证
    java -version
}

Maven(){
    # 下载
    wget -c https://archive.apache.org/dist/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz -O $ZIP_PATH/apache-maven-3.5.0-bin.tar.gz
    # 解压
    tar -zxvf $ZIP_PATH/apache-maven-3.5.0-bin.tar.gz -C $SETUP_PATH

    # 配置环境变量
    echo "export MAVEN_HOME=$SETUP_PATH/apache-maven-3.5.0" >> $ENV_FILE
    echo "export PATH=\${MAVEN_HOME}/bin:\$PATH" >> $ENV_FILE
    # 重载环境变量
    source $ENV_FILE

    # 验证
    mvn -v
}

Tomcat(){
    # 下载
    wget -c https://archive.apache.org/dist/tomcat/tomcat-8/v8.5.23/bin/apache-tomcat-8.5.23.tar.gz -O $ZIP_PATH/apache-tomcat-8.5.23.tar.gz
    # 解压
    tar -zxvf $ZIP_PATH/apache-tomcat-8.5.23.tar.gz -C $SETUP_PATH

    # 配置环境变量
    echo "export CATALINA_HOME=$SETUP_PATH/apache-tomcat-8.5.23" >> $ENV_FILE
    echo "export PATH=\${CATALINA_HOME}/bin:\$PATH" >> $ENV_FILE
    # 重载环境变量
    source $ENV_FILE

    # 解决 Tomcat 启动时间过长的问题
    sudo sed -i "s/\(^securerandom\.source=\)/\# \1/" $JAVA_HOME/jre/lib/security/java.security

    # 验证 && 启动
    catalina.sh version && startup.sh
}

Nginx(){
    # 安装依赖
    sudo apt-get install -y gcc make openssl libssl-dev zlib1g-dev libpcre3 libpcre3-dev
    # 下载
    wget -c http://nginx.org/download/nginx-1.16.1.tar.gz -O $ZIP_PATH/nginx-1.16.1.tar.gz
    # 解压
    tar -zxvf $ZIP_PATH/nginx-1.16.1.tar.gz -C $SOURCE_PATH
    # 进入解压目录
    cd $SOURCE_PATH/nginx-1.16.1
    # 预编译
    ./configure --prefix=$SETUP_PATH/nginx-1.16.1
    # 编译和安装
    make && make install || make clean

    # 配置环境变量
    echo "export NGINX_HOME=$SETUP_PATH/nginx-1.16.1" >> $ENV_FILE
    echo "export PATH=\${NGINX_HOME}/sbin:\$PATH" >> $ENV_FILE
    # 重载环境变量
    source $ENV_FILE

    # 验证 & 启动
    sudo $NGINX_HOME/sbin/nginx -v
    sudo $NGINX_HOME/sbin/nginx
}

Vsftpd(){
    sudo apt-get -y update && sudo apt-get install -y vsftpd
}

MySQL(){
    # 下载 APT 配置软件
    wget -c https://repo.mysql.com/mysql-apt-config_0.8.14-1_all.deb -O $ZIP_PATH/mysql-apt-config_0.8.14-1_all.deb
    # 安装 APT 配置软件
    sudo dpkg -i $ZIP_PATH/mysql-apt-config_0.8.14-1_all.deb

    # 安装 MySQL
    sudo apt-get update -y && sudo apt-get install mysql-server -y --allow-unauthenticated
    # sudo sed -i "/\[mysql\]/a\default-character-set=utf8" /etc/mysql/conf.d/mysql.cnf
    sudo sed -i "/\[mysqld\]/a\character-set-server=utf8" /etc/mysql/mysql.conf.d/mysqld.cnf

    # 启动交互式安全配置
    sudo mysql_secure_installation
}

VSFTPD_CONFIG(){
    CONF_VSFTPD_SRC=$MALL_PATH/prod/conf/vsftpd.conf
    CONF_VSFTPD_DES=/etc/vsftpd.conf

    # 更换配置
    sudo cp $CONF_VSFTPD_SRC $CONF_VSFTPD_DES
    # 重载配置
    sudo service vsftpd reload
    # 添加用户
    [ -z "$(grep ^/sbin/nologin$ /etc/shells)" ] && echo /sbin/nologin | sudo tee -a /etc/shells
    sudo useradd -m -d /home/mall mall -s /sbin/nologin
    echo mall:mall_ssap|sudo chpasswd
    echo mall | sudo tee -a /etc/user_list
}

NGINX_CONFIG(){
    source $ENV_FILE

    CONF_FILE_SRC=$MALL_PATH/prod/conf/file.epoch.fun.conf
    CONF_MALL_SRC=$MALL_PATH/prod/conf/mall.epoch.fun.conf
    CONF_FILE_DES=$NGINX_HOME/conf/vhost/file.epoch.fun.conf
    CONF_MALL_DES=$NGINX_HOME/conf/vhost/mall.epoch.fun.conf

    sed -i "/#user/c\user root;" $NGINX_HOME/conf/nginx.conf
    sed -i "/# another/i\include vhost\/\*\.conf;" $NGINX_HOME/conf/nginx.conf
    mkdir -p $NGINX_HOME/conf/vhost && cp $CONF_FILE_SRC $CONF_FILE_DES && cp $CONF_MALL_SRC $CONF_MALL_DES

    sudo $NGINX_HOME/sbin/nginx -s reload
}

MYSQL_CONFIG(){
    SQL_MALL=$MALL_PATH/docs/sqls/mall.sql
    SQL_DATA=$MALL_PATH/docs/sqls/data.sql

    read -r -p "Please input the username of mysql: " username
    read -r -p "Please input the password of mysql: " password
    mysql -u$username -p$password -e "source $SQL_MALL"
    mysql -u$username -p$password -e "source $SQL_DATA"
    mysql -u$username -p$password -e "CREATE USER 'mall'@'localhost' IDENTIFIED BY 'mall_pass';"
    mysql -u$username -p$password -e "GRANT ALL PRIVILEGES ON mall.* TO mall@localhost IDENTIFIED BY 'mall_pass';"
}

source $ENV_FILE

ALL(){
    java -version >> $MALL_PATH/init.log || JDK
    mvn -v >> $MALL_PATH/init.log || Maven
    version.sh >> $MALL_PATH/init.log || Tomcat
    vsftpd -v >> $MALL_PATH/init.log || (Vsftpd && VSFTPD_CONFIG)
    sudo $NGINX_HOME/sbin/nginx -v >> $MALL_PATH/init.log || (Nginx && NGINX_CONFIG)
    mysql -V >> $MALL_PATH/init.log || (MySQL && MYSQL_CONFIG)
}

menu(){
    echo "Usage: $SELF_PATH <all>       --  install all"
    echo "Usage: $SELF_PATH <jdk>       --  install JDK"
    echo "Usage: $SELF_PATH <mvn>       --  install Maven"
    echo "Usage: $SELF_PATH <tom>       --  install Tomcat"
    echo "Usage: $SELF_PATH <ftp>       --  install Vsftpd & config"
    echo "Usage: $SELF_PATH <nginx>     --  install Nginx & config"
    echo "Usage: $SELF_PATH <mysql>     --  install MySQL & config"
    echo "Usage: $SELF_PATH <nginxc>    --  config Nginx"
    echo "Usage: $SELF_PATH <mysqlc>    --  config MySQL"
}

case $1 in
    jdk)    JDK     ;;
    mvn)    Maven   ;;
    tom)    Tomcat  ;;
    ftp)    Vsftpd  && VSFTPD_CONFIG    ;;
    nginx)  Nginx   && NGINX_CONFIG     ;;
    mysql)  MySQL   && MYSQL_CONFIG     ;;
    nginxc) NGINX_CONFIG                ;;
    mysqlc) MYSQL_CONFIG                ;;
    all)    ALL     ;;
    *)      menu    ;;
esac