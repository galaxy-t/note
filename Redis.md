# Redis

## 单机安装

### Windows 安装使用

       https://github.com/microsoftarchive/redis/releases
       
       下载 Redis-x64-3.0.504.zip 并解压 
       Dos 进入该解压目录
       执行命令 redis-server.exe redis.windows.conf 启动 redis , 后面的 redis.windows.conf 可以省略 , 如果省略 , 会启用默认的
       执行之后的界面不要关闭
       
       测试
       redis-cli.exe -h 127.0.0.1 -p 6379   使用该命令登录本地命令行客户端
       
### Linux 安装和使用

    1: 获取 redis 资源
    
        wget http://download.redis.io/releases/redis-5.0.5.tar.gz
        
    2: 解压
    
        tar -zxvf redis-5.0.5.tar.gz
            
    3: 编译
    
        cd redis-5.0.5 进入到 redis 根目录
        make  编译源代码
        若编译出错 , 则是因为本机未安装 gcc , 执行 yum install gcc
        然后再执行 make 就可以完成
        
    4: 安装
        
        cd /root/redis-5.0.5/src    进入到 src 目录
        make install PREFIX=/usr/local/redis    执行该命令进行安装 , 将 redis 安装到  /usr/local/redis 目录下
        
    5: 配置
    
        cd /usr/local/redis      进入到 redis 安装目录的根目录
        mkdir etc                   创建一个 etc 文件夹
        mv /root/redis-5.0.5/redis.conf /usr/local/redis/etc/   将 redis 提供的配置文件移动到新建的 etc 目录下
  
    6: 启动
        
        cd /usr/local/redis/bin/   进入到  redis 安装目录的 bin 目录下 
        ./redis-server /usr/local/redis/etc/redis.conf  执行该命令并指明配置文件
        
## 集群部署

### 主从模式

    主从配置最少需要两个 redis 服务 , 一个为主数据库(master) , 另一个为从数据库(slave) , 在主从模式中主数据库只可存在一个 , 从数据库可存在多个
    
     vim /usr/local/redis/etc/redis.conf    修改配置文件
     replicaof 127.0.0.1 6379               这行配置默认是注释掉的 , 将注释去掉 , 修改主数据库的 ip 和端口即可
     若主数据库设置过密码 , 则将下面几行的  masterauth <master-password> 配置的注释也去掉 , 将主数据库的密码配置上即可
     配置好之后启动主数据库和从数据库即可自动形成主从模式 , 尤其注意 从数据库默认只提供读的功能 , 不能够提供写功能
     主数据库做出修改之后会自动异步将数据同步到从数据库
     master 不能提供服务之后整个主从只可提供读操作,不能够再提供写操作
     slave 停止提供服务后不影响其他 slave 的读和 master 的读写 , 重启后会将数据从 master 中同步过来
     
     工作机制
     当 slave 启动后 , 会主动向 master 发送 SYNC 命令 , master 接收到 SYNC 命令后会在后台保存快照 (RDB持久化) 和缓存保存快照这段时间的命令
     然后将保存的快照文件和缓存的命令发送给 slave . slave 接收到快照文件和命令后加载快照文件和缓存的执行命令 . 复制初始化之后 , master 每次接收到的
     写命令都会同步到 slave , 保证主从数据一致性.
     
### 哨兵模式
    
    哨兵模式就是在主从模式的基础上为主从模式添加了选举功能 , 即在 master 不能提供服务的情况下 , 哨兵服务会在其他可用的 slave 中选举一个座位 master 使用
    当前面的 master 重新提供服务后其会自动降级为 slave
    
    cp /root/redis-5.0.5/sentinel.conf /usr/local/redis/etc/        首先将 redis 提供的默认的哨兵配置文件拷贝到安装目录下
    vim sentinel.conf           修改这个配置文件
    找到 sentinel monitor mymaster 127.0.0.1 6378 2 这一句   修改为自己的配置即可  mymaster 是自定义的名字可随意修改 后面是 master 的 ip 和端口 , 最后面的 2 的意思是
    至少有两台或两台以上的哨兵认为 master 不可用时候才进行重新选举
    
    ./redis-sentinel /usr/local/redis/etc/sentinel.conf     执行该命令启动哨兵 
    
    由上可以看出 , 哨兵模式其实是在主从模式的基础上又新开了一个服务作为监控服务使用 , 哨兵自己也可以组成集群使用
    
    工作机制
    每个 sentinel 以每秒钟一次的频率向它所知的 master,slave 以及其他 sentinel 实例发送一个 PING 命令
    如果一个实例距离最后一次有效回复 PING 命令的时间超过 down-after-milliseconds 选项所指定的值， 则这个实例会被 sentinel 标记为主观下线
    如果一个 master 被标记为主观下线，则正在监视这个 master 的所有 sentinel 要以每秒一次的频率确认 master 的确进入了主观下线状态
    当有足够数量的 sentinel（大于等于配置文件指定的值）在指定的时间范围内确认 master 的确进入了主观下线状态， 则 master 会被标记为客观下线
    在一般情况下 , 每个sentinel会以每 10 秒一次的频率向它已知的所有 master,slave 发送 INFO 命令
    当 master 被 sentinel 标记为客观下线时 , sentinel 向下线的 master 的所有 slave 发送 INFO 命令的频率会从 10 秒一次改为 1 秒一次
    若没有足够数量的 sentinel 同意 master 已经下线 , master 的客观下线状态就会被移除
    若 master 重新向 sentinel 的 PING 命令返回有效回复 , master 的主观下线状态就会被移除
    
### Cluster 模式

    哨兵模式基本上都能满足一般生产的需要 , 具备高可用性 . 但当数据量过大 , 一台服务器存放不下的情况时 , 主从模式或哨兵模式就不能满足需求了
    这时候需要对存储的数据进行分片 , 将数据存储到多个 redis 实例中 . cluster 模式的出现就是为了解决单机 redis 容量有限的问题 , 将 redis 的数据根据
    一定的规则分配到多台机器 . 其提供主从和选举机制 , 如一个 2 * 3 的集群其意义为全部数据一共有 3 组切片 , 每组切片都有一主一从存在
    redis 要求每个集群中至少存在三个主数据库 , 那么就至少需要六个 redis 数据库才能组成 cluster 模式
    
    部署,在一台电脑上部署
    根据单机模式照常安装即可 , 安装完之后把 bin 目录的文件全部移动到 /usr/local/redis 目录中  bin 目录删除
    然后在该目录中执行
    mkdir 7000 7001 7002 7003 7004 7005
    然后以上六个文件夹中每一个文件夹中放一个 redis.conf
    其配置可以简化为
    port 7000
    cluster-enabled yes
    cluster-config-file nodes.conf
    cluster-node-timeout 5000
    appendonly yes
    注意每个文件夹中的端口都要跟文件夹名一致
    然后类似于下面这样
    cd 7000
    ../redis-server ./redis.conf
    将六个服务全部启动
    
    最后再 /usr/local/redis 目录中执行
    ./redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 --cluster-replicas 1
    最后面那个 1 的意思是为每一个 master 分配一个 slave
    
    然后随意登录某一台 redis server 的命令行
    ./redis-cli -c -p 7000      -c 的意思是集群模式进入
    执行 cluster nodes 即可查看全部节点
    
    若出现全部停机的状态 , 则只需要启动全部的节点即可 , 不需要再执行 ./redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 --cluster-replicas 1
    
    注
    多个 redis 节点网络互联 , 数据共享.
    所有的节点都是一主一从(也可以一主多从) , 其中从不提供服务 , 仅作为备份
    不支持同时处理多个 key (如 : MSET/MGET) , 因为 redis 需要把 key 均匀分布在各个节点上 , 并发量很高的情况下 , 同时创建 key-value 会降低性能并导致不可预测的行为
    支持在线增加,删除节点
    客户端可以连接任何一个主节点进行读写
    
  
## 配置

### 修改 redis 端口 , 单机多开

    vim /usr/local/redis/etc/redis.conf 
    修改配置文件中的 port 端口即可

### 命令行客户端

    cd /usr/local/redis/bin/   进入到  redis 安装目录的 bin 目录下
    ./redis-cli                 执行该命令

### 设置后台运行

    vim /usr/local/redis/etc/redis.conf
    将 daemonize 从 no 改为 yes      
       
### 设置密码

    CONFIG get requirepass  通过该命令查看是否设置了密码
    CONFIG set requirepass "123456"        设置密码为 123456
    设置密码后如果通过命令行连接成功后是操作不了 redis 的 , 需要执行 AUTH "123456" 来验证密码才能正常执行
    
    