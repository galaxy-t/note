# Kafka

    
    
    
    
## 单机安装

### 下载

    https://kafka.apache.org/downloads
    到上面的地址下载一个稳定版本 , 注意是要下载编译好的 , 如 : kafka_2.12-2.5.0.tgz
    
### 解压

    tar -zxvf kafka_2.12-2.5.0.tgz
    
### 将解压后的文件夹移动到 /usr/local 并重命名

    mv kafka_2.12-2.5.0 /usr/local/kafka
    
### 启动其自带的 zookeeper

    cd /usr/local/kafka/bin
    终端运行
    ./zookeeper-server-start.sh /usr/local/kafka/config/zookeeper.properties
    后台运行
    ./zookeeper-server-start.sh -daemon /usr/local/kafka/config/zookeeper.properties
### 自带的 zookeeper 参数配置

    cd /usr/local/kafka/config
    vim zookeeper.properties
    
    可以自行配置数据存储的目录 , 客户端端口 等
     
    若服务器内存较小 , 也可编辑 zookeeper 的启动文件    vim /usr/local/kafka/bin/zookeeper-server-start.sh
    将其中的 512 调小即可

### 关闭 zookeeper 

    cd /usr/local/kafka/bin
    ./zookeeper-server-stop.sh -daemon config/zookeeper.properties
    
    若窗口运行的话直接 ctrl + c 即可
    
### kafka 参数配置

    cd /usr/local/kafka/config
    vim server.properties
    
    简单配置不需要修改什么即可启动
    
### 启动 kafka 

    cd /usr/local/kafka/bin
    终端运行
    ./kafka-server-start.sh /usr/local/kafka/config/server.properties
    后台运行
    ./kafka-server-start.sh -daemon /usr/local/kafka/config/server.properties
     
### 停止 kafka

    cd /usr/local/kafka/bin
    ./kafka-server-stop.sh config/server.properties
    
    终端运行只需要 ctrl + c 即可
    
### 创建 topic

    cd /usr/local/kafka/bin
    ./kafka-topics.sh --create --zookeeper 127.0.0.1:2181 --replication-factor 1 --partitions 1 --topic test
    指明 zookeeper 的地址及端口 , 设置主题名称为 test
    
### 查看 topic

    cd /usr/local/kafka/bin
    ./kafka-topics.sh --list --zookeeper 127.0.0.1:2181
    
### 发送消息
    
    cd /usr/local/kafka/bin
    ./kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic test
    指明 kafka 的地址及端口并请求连接 test 主题
    
### 接收消息

    cd /usr/local/kafka/bin
    ./kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic test --from-beginning
    指明 kafka 的地址及端口并请求连接 test 主题
    
### 删除主题

    kafka-topics.sh --zookeeper 127.0.0.1:2181 --delete  --topic test