


1. 官网下载最新版本即可，当前使用的是 4.6.0

    > http://rocketmq.apache.org/release_notes/release-notes-4.6.0/
    
2. 解压之后修改两个文件

    > bin/runbroker.sh
    > JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m"
    
    > bin/runserver.sh
    > 跟上面的一样，也是设置的 1g 和 512m ， 还有两个参数没改

    > 这里有个小坑，别弄的比自己电脑的内存大，否则启动不了，默认的就很大，也别弄的小于 1g ，貌似也启动不了
    > 其实也就是这里参考了一下 https://www.cnblogs.com/simplefuer/p/12192984.html ， 其它的配置和使用都是按照官网的文档来的
    
3. 启动 Name Server

    >nohup sh bin/mqnamesrv &
    
    > tail -f ~/logs/rocketmqlogs/namesrv.log
    
4. 启动 Broker

    > nohup sh bin/mqbroker -n localhost:9876 &
    
    > tail -f ~/logs/rocketmqlogs/broker.log
    
5. 停止服务

    > sh bin/mqshutdown broker
    
    > sh bin/mqshutdown namesrv
    
6. rocketmq-console

    > 网上很多教程说下载源码然后编译啥的，费劲，我这有现成的 docker ，直接拉镜像
    
    > https://github.com/apache/rocketmq-externals/tree/master/rocketmq-console 官方教程
    
    > 镜像拉取：docker pull styletang/rocketmq-console-ng
    
    > docker run -d -e "JAVA_OPTS=-Drocketmq.namesrv.addr=192.168.31.170:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false" -p 9877:8080 --name rocketmq-console  styletang/rocketmq-console-ng
    > 别用官方教程上的命令启动，就用我给的这个就行了，已经修改好了
    > 程序默认用的是 8080 端口，左边可以自定义宿主机的端口映射一下
    > 修改一下自己 mq 的地址和端口号
    
    > 然后直接访问 192.168.31.170:9877 即可
    
---

* 一篇将 rocketmq 打成 docker 镜像的文章 https://blog.csdn.net/weixin_37946205/article/details/86617965


## 版本 5.12.4

1. 下载解压后依然修改 bin 目录下面的 runbroker.sh和runserver.sh 两个文件, -Xms256m -Xmx256m -Xmn128m 三个参数这样设置即可
2. 开启远程访问, 修改 conf/broker.conf, 添加两行 namesrvAddr = xx.xx.xx.xx:9876 和 brokerIP1 = xx.xx.xx.xx, 其中 xx.xx.xx.xx 为部署服务器的公网 ip 地址
3. 启动 namesrv, nohup sh bin/mqnamesrv -n 121.43.144.125:9876 &
4. 验证 namesrv 是否启动成功, tail -f ~/logs/rocketmqlogs/namesrv.log
5. 启动 broker + proxy, nohup sh bin/mqbroker -n 121.43.144.125:9876 -c conf/broker.conf  --enable-proxy &
6. 验证是否启动成功, tail -f ~/logs/rocketmqlogs/proxy.log 
7. 关闭服务, sh bin/mqshutdown broker 和  sh bin/mqshutdown namesrv
8. 注: 默认使用 jdk8 启动不会报错, 用 jdk17 会出现各种各样恶心的问题

