# MySQL

## 添加 mysql 安装源

    现在 yum 或 dnf 没有自带 mysql 的安装源, 需要自己手动将 mysql 的安装源安装到本地.
    https://repo.mysql.com/ 这个网站列出了所有的 mysql 安装源, 可以自己选择需要的版本将该源添加到本地.

### mysql 安装源

    mysql80-community-release-el9.rpm
    mysql80-community-release-el9-5.noarch.rpm
    mysql80-community-release-el9-4.noarch.rpm

    以上以 mysql8 为例, 列出了 mysql8 版本的三个安装源, 以下拆解每一段的含义:
    mysql80 指的是 mysql的版本为 8.
    community 指的是社区版.
    el9 这个要特别注意, 它指的是这个安装源对应的 mysql 包所匹配的 linux 系统的发行版版本, el 是 Red Hat Enterprise Linux (RHEL) 的简写, 这个可以自行检查自己 linux 系统对应的 el 版本.
    el9-5 后面这个 5 指的是 RHEL 的小版本, 即 RHEL9.5 版本, 一般来说不同的小版本是通用的, 但基本上都会出现各种各样的问题, 最好还是确定到小版本来安装.
    noarch 代指 无特定架构, 适用于所有的 CPU 平台, x86_64 或 aarch64 都可以安装.

### 安装源

    以 amazon/al2023-ami-2023.6.20250303.0-kernel-6.1-x86_64 为例
    切换到 root 用户, 直接执行以下命令

    dnf install -y https://repo.mysql.com/mysql80-community-release-el9.rpm

#### 查看已安装的数据源

    列出所有已安装的数据源
    dnf repolist

    查看数据源配置文件
    cd /etc/yum.repos.d/
    ls
    vim mysql-community.repo
    确保 enabled = 1

## 安装 mysql

    dnf install mysql-community-server
    直接各种 yes

## 操作 mysql 状态  =eka>4xqUyjT

    查看运行状态
    systemctl status mysqld
    启动
    systemctl start mysqld
    停止
    systemctl stop mysqld

### 首次运行会生成一个临时密码, 使用以下命令进行查看

    sudo grep 'temporary password' /var/log/mysqld.log

### 很多教程会说让使用 mysql_secure_installation 进行首次安全配置, 可以不做这些配置, 直接 mysql -uroot -p 然后使用临时密码登录即可

## 修改 root 密码
    
    修改本地 root 用户密码
    ALTER USER 'root'@'localhost' IDENTIFIED BY '你的密码'
    注: 这个密码安全等级要求还是挺高的, 各种特殊字符,大小写,数字之类的都要有, 自己多试验几次

    删除所有的临时用户
    DELETE FROM mysql.user WHERE User=''

    刷新权限
    FLUSH PRIVILEGES

## 开通远程访问权限

    确保 root 在本地仍然可用，并使用 mysql_native_password 认证方式, 那这么看, 上面那个改密码的操作纯属多余, 但是我还是做了
    ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '你的密码'

    允许 root 远程访问, 在 mysql 库的 user 表中会多一条 'root'@'%' 记录, 其密码也使用 mysql_native_password 的认证方式
    CREATE USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '你的密码'

    给 root 最高权限
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION

    使修改立即生效
    FLUSH PRIVILEGES

