
### 拉取镜像，本次使用的版本是 8.0.19

    docker pull mysql:8.0.19
    
### 通过镜像创建容器

    docker run -di --name=mysql8 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql:8.0.19
    
    -p 代表端口映射，格式为 宿主机映射端口:容器运行端口
    -e 代表添加环境变量 MYSQL_ROOT_PASSWORD 是 root 用户的登陆密码
    
### 登录容器
    
    docker exec -it mysql8 /bin/bash

### 容器内登陆 MYSQL

    mysql -p 123456
    
### 修改 root 用户拥有远程访问权限 , 并设置 root 用户远程登陆密码
    
    ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
    
### 刷新权限

    flush privileges;
    
### 退出 mysql

    exit
    
### 退出容器

    exit
    
    
===

### 以下作为记录 , 不需要实际操作
    
    
### ALTER USER 'root'@'localhost' IDENTIFIED BY '123456' PASSWORD EXPIRE NEVER;

    修改加密规则
    
### ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';

    修改密码
    
### GRANT ALL ON *.* TO 'root'@'%';

    开启远程访问权限
    
### flush privileges;

    刷新权限
    
### 必要的时候关闭防火墙