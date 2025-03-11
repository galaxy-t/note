# Nginx

## 下载 nginx 源吗

1. 打开 https://nginx.org/en/download.html 网址, 复制当前稳定版本的下载地址
2. wget https://nginx.org/download/nginx-1.26.3.tar.gz 下载到本地
3. 解压 tar -zxvf nginx-1.26.3.tar.gz

## 安装编译工具

1. dnf -y install gcc-c++
2. dnf install -y pcre-devel zlib-devel openssl-devel

## 编译安装

1. cd nginx-1.26.3
2. ./configure
3. make
4. make install

## 运行

1. cd /usr/local/nginx/sbin
2. ./nginx

## 检查是否运行成功

1. 浏览器打开 http://服务器ip:80

