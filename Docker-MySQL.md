
### docker pull mysql:8.0.19
    
    拉取镜像，本次使用的版本是 8.0.19
    
### docker run -di --name=mysql8 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=Q1W2E3**# mysql
    
    通过镜像创建容器
    -p 代表端口映射，格式为 宿主机映射端口:容器运行端口
    -e 代表添加环境变量 MYSQL_ROOT_PASSWORD 是 root 用户的登陆密码
    
### docker exec -it mysql /bin/bash

    登录容器
    
### ALTER USER 'root'@'localhost' IDENTIFIED BY '123456' PASSWORD EXPIRE NEVER;

    修改加密规则
    
### ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'Q1W2E3**#';

    修改密码
    
### GRANT ALL ON *.* TO 'root'@'%';

    开启远程访问权限
    
### flush privileges;

    刷新权限
    
### 必要的时候关闭防火墙