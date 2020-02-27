# Linux 一些常用命令

### 防火墙

1. 查看防火墙状态

    > systemctl status firewalld

2. 查看开机是否启动防火墙服务

    > systemctl is-enabled firewalld

3. 关闭防火墙

    > systemctl stop firewalld
           
    > systemctl status firewalld


4. 禁用防火墙（系统启动时不启动防火墙服务）

    > systemctl disable firewalld
                            
    > systemctl is-enabled firewalld