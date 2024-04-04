
# Classic

1. 下载镜像(最新版本)

    > docker pull webcenter/activemq:latest
    
2. 启动并指定端口号及管理平台账号密码

    > docker run -d --name='activemq' -e 'ACTIVEMQ_ADMIN_LOGIN=gambleractivemq' -e 'ACTIVEMQ_ADMIN_PASSWORD=a17784574' -e 'ACTIVEMQ_CONFIG_SCHEDULERENABLED=true' -p 8161:8161 -p 61616:61616 webcenter/activemq:latest

    > docker run -d --name='activemq' -p 8161:8161 -p 61616:61616 webcenter/activemq:latest


3. 管理后台端口
   
   8161

4. 消息队列服务端口

   61616
    
# Artemis

1. 拉取镜像
   
   > docker pull apache/activemq-artemis:2.31.2

2. 启动

   > docker run -d --name activemq -p 61616:61616 -p 8161:8161 apache/activemq-artemis:2.31.2

