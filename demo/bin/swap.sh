#!/bin/bash
# ============================== ⬇⬇⬇  无需改动 ⬇⬇⬇ ==============================
# 获取脚本名称
SELF_NAME=$(basename $BASH_SOURCE)
# 获取脚本路径
SELF_PATH=$(cd `dirname $0` && pwd)/$SELF_NAME
# ============================== ⬆⬆⬆  无需改动 ⬆⬆⬆ ==============================

FILE_NAME=swapfile
SWAP_FILE=/root/$FILE_NAME
SWAP_FILE_ESCAPE="\/root\/$FILE_NAME"

enable(){
    [ -n "$1" ] && SIZE=$1 || SIZE=1024
    # 创建交换文件
    sudo touch $SWAP_FILE
    # 设置文件空间 1024M = 1G
    sudo dd if=/dev/zero of=$SWAP_FILE bs=1M count=$SIZE
    # if    input_file  输入文件
    # of    output_file 输出文件
    # bs    block_size  块大小
    # count             块计数
    # 格式化交换文件
    sudo mkswap $SWAP_FILE
    # 启用交换文件
    sudo swapon $SWAP_FILE
    # 开机自动加载虚拟内存
    echo "$SWAP_FILE swap swap defaults 0 0" | sudo tee -a /etc/fstab
    # 重启后生效
    reboot_now
}

disable(){
    # 停用交换分区
    sudo sed -i "/$SWAP_FILE_ESCAPE/d" /etc/fstab
    # 停用交换文件
    sudo swapoff $SWAP_FILE
    # 重启生效
    reboot_now
    # 删除交换文件
    echo "Please perform 'sudo rm -rf $SWAP_FILE' manually after reboot."
}

reboot_now(){
    read -r -p "Are you sure to reboot now? [Y/n] " input
    [ "$input" == "yes" ] || [ "$input" == "Y" ] || [ "$input" == "y" ] && sudo reboot
}

menu(){
    echo "Usage: $SELF_NAME <enable> <size/M>"
    echo "Usage: $SELF_NAME <disable>"
    echo "Usage: $SELF_NAME <reboot>"
}

case $1 in
    enable)     enable $2   ;;
    disable)    disable     ;;
    reboot)     reboot_now  ;;
    *)          menu        ;;
esac