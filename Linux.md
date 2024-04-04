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



### 防火墙2

1. 启动防火墙

    > systemctl start firewalld.service

2. 关闭防火墙

    > systemctl stop firewalld.service

3. 添加放行端口

    > firewall-cmd --zone=public --add-port=3306/tcp --permanent （--permanent永久生效，没有此参数重启后失效）

4. 删除放行端口

    > firewall-cmd --zone=public --remove-port=2375/tcp --permanent

5. 重载防火墙

    > firewall-cmd --reload

6. 查看放行端口

    > iptables-save

### 查看端口占用情况

   > netstat -tlunp
