# Kafka

    一种高吞吐量的分布式发布订阅消息系统
    通过O(1)的磁盘数据结构提供消息的持久性 , 这种结构对于数据以 TB 的消息存储也能够保持长时间的稳定性能
    高吞吐量 : 及时是非常普通的硬件 , Kafka 也可以支持每秒数百万的消息
    
    Kafka 的消息可以自行设置保留策略 , 如将保留策略设置为两天 , 则在发布记录后的两天内该记录可供使用 , 之后将被丢弃以释放空间 , Kafka 的性能相对于数据大小实际上是恒定的 , 因此长时间存储数据不是问题
    
    相关术语
    Broker(经纪人)
    Kafka 集群包含一个或多个服务器 , 这种服务器被称为 Broker
    
    Topic(话题)
    每条发布到 Kafka 集群的消息都属于一个类别 , 这个类别被称为 Topic
    物理上不同 Topic 的消息分开存储 , 逻辑上一个 Topic 的消息虽然保存在一个或多个 Broker 上 , 但用户只需要指定消息的 Topic 即可生产或消费数据 , 而不必关心数据存于何处
    
    可以简单理解一个 Topic 就是一个队列
    
    Partition
    Partition 是物理上的概念 , 每个 Topic 包含一个或多个 Partition
    
    在创建 Topic 的时候设置该 Topic 有几个 Partition , 该 Topic 收到的消息会按规则放到不同的 Partition 中 , 如果生产者发送的消息没有 Key , 则消息会均匀分不到所有的 Partition ,
    若存在 Key , 则会根据 key 进行某种规则的存储
    
    Producer
    负责发布消息到 Kafka broker
    
    Consumer
    消息消费者 , 向 Kafka broker 读取消息的客户端
    
    Consumer Group
    每个 Consumer 属于一个特定的 Consumer Group , 可为每个 Consumer 指定 GroupName , 若不指定 GroupName 则属于默认的 group
    发布到主题的每条消息都会传递到关注该主题的每个订阅消费者组中的一个消费者实例
    如果所有消费者都具有相同的消费者组 , 那么将在这些消费者中有效的平衡记录
    如果消费者实例具有不同的消费者组 , 则每条记录将广播到所有消费者进程
    
    Replica 
    备份数量 , 针对 Topic 备份几次 , 备份的单位为 Partition , 如现在有三个 Broker , 一个 Topic , 三个 Partition , 若这是该 Topic 的 Replica 的数量为 2 则会在三个 Broker 上
    每个有两个 Partition , 且各不相同 , 此时起到了备份的作用 , 也仅仅是备份 , 该备份不提供读写的能力
    
    同一个 CG 中的不同 Consumer 都只对应一个 Topic 中的一个 Partition , 所以 CG 中 Consumer 的数量不要大于 Topic 中 Partition 的数量 , 否则多余的收不到消息
    如果想有多个租户就需要设置该 Topic 的 Partition 的数量 , 这样 Consumer 的数量才可以增加
    
    
    
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
    ./kafka-topics.sh --create --zookeeper 127.0.0.1:2181 --replication-factor 1 --partitions 2 --topic test
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
    
## 集群

    Kafka 的集群其实就是多个 Kafka 的节点注册到一台或者一组 Zookeeper 上 , 注意每个 Kafka 的节点的配置 , 修改端口 , 修改文件存储路径等
    由此可见 Kafka 的集群更重要的是保证 Zookeeper 的高可用 , 即 Zookeeper 集群
    