#Elasticsearch

## 基本概念

### 全文检索(Full-text Search)

    全文检索是指计算机索引程序通过扫描文章中的每一个词 , 对每一个词建立一个索引 , 指明该词在文章中出现的次数和位置 , 
    当用户查询时 , 检索程序就根据事先建立的索引进行查找 , 并将查找的结果反馈给用户的检索方式 .

### 倒排索引(Inverted Index)

    该索引表中的每一项都包括一个属性值和具有该属性值的各记录的地址 . 由于不是由记录来确定属性值 , 而是由属性值来确定记录的位置 , 
    因此被称为倒排索引 . Elasticsearch 能够实现快速 , 高效的搜索功能 , 正式基于倒排索引原理

### 近实时性

    这意味着导入一个文档到这个文档可以被搜索是存在轻微的延迟

### 集群 (Cluster)

    一个集群是由一个或多个节点(服务器)组成的 , 通过所有的节点一起保存你的全部数据并且提供联合索引和搜索功能的节点集合 . 
    每个集群有一个唯一的名称标识 , 默认是 "elasticsearch" . 这个名称非常重要 , 因为一个节点 (Node) 只有设置了这个名称才能
    加入集群 , 成为集群的一部分
    
    一个集群中只有一个节点是有效的 , 所以可能需要部署多个集群并且每个集群有它们唯一的集群名称
    
### 节点 (Node)

    一个节点是一个单一的服务器 , 是集群的一部分 , 存储数据并且参与集群的索引和搜索功能 . 跟集群一样 , 节点在启动时也会被分配一个唯一的标识名称 , 
    这个名称模式是一个随机的 UUID . 如果不想用默认的 , 也可以自己定义节点的名称 . 这个名称对于集群管理节点识别哪台服务器对应集群中的哪个节点
    有重要的作用
    
### Index

    Elasticsearch 数据管理的顶层就叫做 Index(索引) 它类似于 MySQL 中的表 , 有的说它类似于 MYSQL 中的数据库的概念 , 我总感觉它像是表
    每个 Index 的名字必须是小写
    
### Type

    Document 可以分组 , 也仅仅作为标识使用 , 即用于快速的对同一个 Index 中的 Document 进行分类
    如 MySQL 中某张表中 有 sex 字段 , 可以很容易的通过这个字段将全部的数据进行区分
    
    这个概念比较模糊与 Index 有点模糊 , 可以理解为不同的 Type 应该有相同的结构 , 即他们都是一样的 Document ,  
    性质完全不同的数据应该存在两个 Index , 而不是一个 Index 里面的两个 Type , 虽然可以做到.
    
    感觉在实际使用的时候 Type 的作用应该不大 , 或许现在我发现不到
    
    根据规划 , Elasticsearch 6.* 版本 只允许每个 Index 包含一个 Type , 7.* 版本会彻底移除 Type
    
### Document

    Index 里面单条的记录称为 Document(文档) , 许多条 Document 构成了一个 Index
    Document 使用 JSON 格式表示 
    同一个 Index 里面的 Document , 不要求有相同的结构 , 但最好保持相同 , 这样有利于提高搜索效率
    
    虽然一个 Document 在物理存储上属于一个 Index , 但是文档实际上必须制定一个索引中的类型

    
### Fields

    字段 , 每个 Document 都类似于一个 JSON 结构 , 它包含了许多字段 , 每个字段都有其对应的值 , 多个字段组成了一个 Document , 可以类比 MYSQL 表中的字段
    
## Shard 和 Node

    Primary Shard(主分片)
    Replica Shard(副分片)
    
    增加或减少节点时 , 会自动均衡
    
    Master 节点 , 主节点 , 其职责是和集群操作相关的内容 , 如创建或删除索引 , 跟踪哪些节点是集群的一部分 , 并决定哪些分片分配给相关的节点 . 稳定的主节点对集群是非常重要的 .
    
    每个节点都能接收请求 , 每个节点接收到请求后都能够把请求路由到相关数据的其他节点上 , 接收原始请求的节点负责采集数据并返回给请求方 . 
    
    Elasticsearch 默认会为每一个 Index 进行分片(默认是 5 个主分片 , 每一个主分片有 1 个副分片 , 即一共有 10 个分片)
    每个分片都具有完整的建立索引和处理请求的能力
    每一个 Document 只存在于某一个主分片以及其对应的副分片中 , 不可能存在于多个主分片中
    副分片是主分片的副本 , 负责容错以及承担读请求负载 , 在主分片不可用的情况下才会承担写操作
    主分片的数量在创建索引的时候就固定了 , 副分片的数量可以随时改
    主分片不能和自己的副分片放在同一个节点上 , 但是可以和其他主分片的副分片放在同一个节点上 , 所以在单节点的环境下只会有主分片存在 , 另外的副分片是无法分配的 , 所以单节点的健康检查是黄色
    可以把副分片当做垂直备份来看
    
    以下 A,B,C,D,E,F 代表不同的节点 a,b,c 代表主分片 (a)(b)(c) 代表副分片
    
    A[a,b,c]    如果只有 A 节点 , A 节点上只会有主分片 a,b,c ,称之为单节点 , 副分片不会被允许和主分片在同一台服务器上
    
    A[a,b,c]
    B[(a),(b),(c)]  如果有 A,B 两个节点 , 其分片的分配大概是这个样子的 , 当有一个节点挂了另一个节点还是能够完整的提供全部数据
    
    A[a,b,(c)]
    B[(a),(b),c]    当然 A,B 两个节点的分片分配也可能是这个样子的
    
    扩容的极限
    A[b]
    B[(b)]
    C[a]
    D[(c)]
    E[c] 
    F[(a)]  如果有 6 个分片 , 则扩容的极限就是 6 个节点 , 每个节点上一个分片 , 如果想超出扩容的极限 , 则可以增加副分片的数量 
    
    A[a(c)]
    B[b(a)]
    C[c(b)] 6 个分片 , 3 个节点 , 最多也只能允许一台服务器宕机 , 如果有两台服务器宕机 , 则查询到的数据就是不完整的 
    
    A[a,b,(c)]
    B[c,(a),(b)]
    C[(a),(b),(c)]  9 个分片 , 3 个节点 , 则最多可以允许三台服务器宕机  
    
## 单机安装

    使用版本 elasticsearch-6.8.8.tar.gz
    
### 下载

    wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.8.8.tar.gz
    
### 解压 移动 重命名

    tar -zxvf elasticsearch-6.8.8.tar.gz
    mv elasticsearch-6.8.8 /usr/local/es

### 修改系统配置文件

    vim /etc/security/limits.conf
    在文件最后添加以下内容
    * soft nofile 65536
    * hard nofile 131072
    * soft nproc 2048
    * hard nproc 4096
    ================================================================
    解释：
    soft  xxx  : 代表警告的设定，可以超过这个设定值，但是超过后会有警告。
    hard  xxx  : 代表严格的设定，不允许超过这个设定的值。
    nofile : 是每个进程可以打开的文件数的限制
    nproc  : 是操作系统级别对每个用户创建的进程数的限制
    ================================================================

    vim /etc/sysctl.conf
    添加如下内容
    vm.max_map_count=655360
    
    加载 sysctl 配置
    sysctl -p
    
### 创建用户和组并授权

    groupadd es         创建用户组 , 名称为 es
    useradd es -g es    创建用户 , 名称为 es , 并归属于 es 组
    chown -R es.es /usr/local/es        为 es 组的 es 用户授权 , 完全控制 /usr/local/es 目录

### 启动

    cd /usr/local/es/bin
    ./elasticsearch     终端运行
    ./elasticsearch -d  守护进程运行
    
### 测试

    curl 127.0.0.1:9200
    
## 集群搭建

    Elasticsearch 的集群相对比较简单以下介绍在同一台服务器上搭建集群和多台服务器上搭建集群
    
### 只有一台服务器 

    将 elasticsearch 解压后的文件夹连续拷贝两份 , 如 /usr/local/es  /usr/local/es1
    为 es 用户组添加两个用户 es es1
    分别为这两个用户授权两个文件夹  chown -R es.es /usr/local/es           chown -R es1.es /usr/local/es1    注意 *.*  点前面的是用户名,后面的是组 我擦
    修改 config 目录下的  elasticsearch.yml
    cluster.name: 这里可以自定义集群名称 , 同一个集群的全部节点名称必须相同
    node.name:    自定义节点名称 , 同一个集群中的不同节点名称必须不同
    http.port: 9200 两个配置文件的这个端口修改一下 , 要求不一样即可
    transport.tcp.port: 9300    这一行是手动添加的 , 也要求端口不一样 , 这是对外提供服务的端口 , 为集群服务的端口
    discovery.zen.ping.unicast.hosts: ["192.168.8.177:9200", "192.168.8.177:9301"]  将全部节点的路径配置进去 , 也不知道对不对 , 反正是跑起来了 , 也没报错
    discovery.zen.minimum_master_nodes: 2   这个是为了避免脑裂 , 选举必须半数加 1
    
    然后分别用两个用户启动两个 es 即可 , 然后就自动组成集群了
    
### 有两台及多台服务器

    每台都照常安装即可
    discovery.zen.ping.unicast.hosts: ["192.168.8.177", "192.168.8.178"]    将这几台的 IP 地址配置上 , 也可以配置域名啥的
    discovery.zen.minimum_master_nodes: 2   这个是为了避免脑裂 , 选举必须半数加 1
    然后每台都启动起来即可

## elasticsearch.yml

    默认情况下 Elasticsearch 只允许本机访问 , 如果需要远程访问
    cd /usr/local/es/config
    vim elasticsearch.yml
    network.host: 192.168.8.177     放开这句并将后面的 ip 修改为本机 ip 地址
    
    cluster.name        集群名称，各节点配成相同的集群名称。
    node.name       节点名称，各节点配置不同。
    node.master     指示某个节点是否符合成为主节点的条件。
    node.data       指示节点是否为数据节点。数据节点包含并管理索引的一部分。
    path.data       数据存储目录。
    path.logs       日志存储目录。
    bootstrap.memory_lock       内存锁定，是否禁用交换。
    bootstrap.system_call_filter    系统调用过滤器。
    network.host    绑定节点IP。
    http.port       端口。
    discovery.zen.ping.unicast.hosts    提供其他 Elasticsearch 服务节点的单点广播发现功能。
    discovery.zen.minimum_master_nodes  集群中可工作的具有Master节点资格的最小数量，官方的推荐值是(N/2)+1，其中N是具有master资格的节点的数量。
    discovery.zen.ping_timeout      节点在发现过程中的等待时间。
    discovery.zen.fd.ping_retries        节点发现重试次数。
    http.cors.enabled               是否允许跨源 REST 请求，表示支持所有域名，用于允许head插件访问ES。
    http.cors.allow-origin              允许的源地址。

## jvm.options

    设置JVM堆大小，一般设置为内存的一半，但最少2G
    sed -i 's/-Xms1g/-Xms2g/' /opt/module/elasticsearch/config/jvm.options
    创建ES数据及日志存储目录并修改属主和属组，与上面配置文件中的路径一一对应
    mkdir -p /data/elasticsearch/data       
    mkdir -p /data/elasticsearch/logs 
    chown -R elasticsearch:elasticsearch /data/elasticsearch  
     #给刚刚创建的目录修改属主和属组
    chown -R elasticsearch:elasticsearch /opt/module/elasticsearch

## 系统优化

    系统优化：
    1.增加最大进程数
     vim /etc/security/limits.conf    
    2.增加最大内存映射数
    vim /etc/sysctl.conf   
    #添加如下
    #elasticsearch用户拥有的内存权限太小，至少需要262144；
    m.max_map_count=262144 
    #表示最大限度使用物理内存，在内存不足的情况下，然后才是swap空间
    vm.swappiness=0			

## REST API

### 集群健康监控

    GET http://192.168.8.177:9200/_cat/health?v
    
    Green - 一切运行正常(集群功能齐全)
    Yellow - 所有数据是可以获取的，但是一些复制品还没有被分配(集群功能齐全)
    Red - 一些数据因为一些原因获取不到(集群部分功能不可用)
    
### 查看集群中的节点列表

    GET /_cat/nodes?v
    
### 查看全部索引

    GET /_cat/indices?v

### 创建一个索引

    PUT http://192.168.8.177:9200/test_index
    后面 test_index 为要新建的做引的名称
    
### 在 test_index 中存入一个 Document , 其 Type 为 doc , Id 为 1

    PUT http://192.168.8.177:9200/test_index/doc/1
    BODY : 
    {
     "name": "John Doe"
    }
    
    注 : 不需要事先创建一个索引 , 在执行插入 Document 时 , 若索引不存在则会自动为其创建
    
### 查看一个 Document 

    GET http://192.168.8.177:9200/test_index/doc/1
    指明 Index , Type 和 Document 的 id 
    
### 删除一个索引

    DELETE http://192.168.8.177:9200/test_index
    删除 test_index 这个索引
    
### 替换一个文档

    PUT http://192.168.8.177:9200/test_index/doc/1
    BODY : 
    {
     "name": "这里是替换的内容"
    }
    与新增 Document 相似 , 只要 id 相同就会将原始的 id 的内容替换成新的内容
    注 : url 中若不指定 id , 则这条内容会作为新增并产生一个随机的 ID
    
### 新增一个文档并随机生成 ID

    POST http://192.168.8.177:9200/test_index/doc
    BODY : 
    {
     "name": "这里是新增的内容"
    }
    这样会创建一条 Document 其随机生成的 ID 大概是这个样子的 xcIzfnIBc4SqretvHnN_
    
### 更新一个文档

    注 : Elasticsearch 底层并没有真正更新文档 , 而是在更新文档时 , 首先删除旧的文档 , 然后加入新的文档
    
    http://192.168.8.177:9200/test_index/doc/xcIzfnIBc4SqretvHnN_/_update?pretty
    {
        "doc":{ "name": "这里是替换的内容11222221"}
    }
    
    以下是将文档修改并添加新的 Field
    http://192.168.8.177:9200/test_index/doc/xcIzfnIBc4SqretvHnN_/_update?pretty
    {
        "doc":{ "name": "这里是替换的内容11222221","age":30}
    }
    
    也可以使用简单的脚本来执行 , 以下使用一个脚本将 age 增加 5
    http://192.168.8.177:9200/test_index/doc/xcIzfnIBc4SqretvHnN_/_update?pretty
    {
        "script" : "ctx._source.age += 5"
    }
    
### 删除一个文档

    DELETE http://192.168.8.177:9200/test_index/doc/1
    
### 检索

    GET http://192.168.8.177:9200/test_index/_search?q=*&sort=age:asc
    以上 url 中 , 请求在 test_index 索引中进行检索(使用_search端点) , 然后 q=* 标识匹配索引中的全部文档 , sort=age:asc 标识 要求年龄正序排列
    
    返回结果:
    took : 执行此次搜索所用的时间(单位: 毫秒)
    timed_out : 本次搜索是否超时
    _shards : 一共检索了多少分片 , 还有搜索成功和搜索失败的分片数量
    hits : 搜索结果
    hits.total : 符合搜索条件的文档数量
    hits.hits : 实际返回搜索结果对象数组 (默认只返回前 10 条)
    hits.sort : 返回结果的排序字段值(如果是按照 score 进行排序 , 则没有)
    hits._score 和 max_score
    
    以下 POST 请求同上面的 GET 请求一样的结果
    POST http://192.168.8.177:9200/test_index/_search
    BODY
    {
    	"query" : {"match_all":{}},
    	"sort": [
    		{"age":"asc"}	
    	]
    }
    
    注 : 一旦请求方得到了返回结果 , Elasticsearch 就完全执行结束 , 不会保持任何的服务器资源或者往返回结果中加入开放的游标
    
### Query DSL

    POST http://192.168.8.177:9200/test_index/_search
    BODY
    {
    	"query" : {"match_all":{}},
    	"sort": [
    		{"age":"asc"}	
    	],
    	"_source" : ["name","age"],
    	"from": 10,
    	"size": 10
    }
    
    query : 定义了查询什么 , match_all 指定了想要查询的类型 , 意思是查询索引中的全部文档
    sort : 排序功能
    size : 指定返回的数量 , 若不指定 , 默认为 10
    from: 指定从第几条开始返回 , 从 0 开始 , 以上是要求返回第 11 条到 20 条文档 , from 默认为 0    
    _source : 指定内容返回哪些属性
    
    {
        "query" : {"match":{"age":18}}
    }
    以上检索条件要求年龄为 18 岁
    {
        "query" : {"match":{"name":"二"}}
    }
    以上检索条件要求姓名中包含 "二" 这个字
    {
        "query" : {"match":{"name":"二 丁"}}
    }
    以上检索条件要求姓名中包含 "王" 或者 "丁" 这个字 , 注意是 或者 不是 和
    {
        "query" : {"match_phrase":{"name":"丁 哈"}}
    }
    以上检索条件用于检索姓名中包含 "丁 哈" 这个短语 , match_phrase 用来查询包含空格的内容
    {
        "query" : {
        	"bool" : {
        		"must" : [
        			{"match":{"name": "王"}},
        			{"match":{"name": "小"}}
        		]
        	}
        }
    }
    以上检索姓名中既包含 "王" 又包含 "小" 的文档 , bool 的 must 子句指定了所有匹配文档必须满足的条件
    {
        "query" : {
        	"bool" : {
        		"should" : [
        			{"match":{"name": "王"}},
        			{"match":{"name": "周"}}
        		]
        	}
        }
    }
    以上检索姓名中包含 "王" 或者 "周" 的文档 , bool 的 should 子语句指定了只要满足其中任一一个条件即可 
    {
        "query" : {
            "bool" : {
                "must_not" : [
                    {"match":{"name": "王"}},
                    {"match":{"name": "周"}}
                ]
            }
        }
    }
    以上检索姓名中既不包含 "王" 又不包含 "周" 的文档 , bool 的 must_not 子语句指定了其中的任何一个条件都不满足即可匹配
    以上我们可以在一个 bool 中同时指定 must , should , must_not , 此外也可以在一个 bool 子句中组合另一个 bool 
    bool 查询也支持 filter 子句 
    {
        "query" : {
            "bool" : {
                "must" : [
                    {"match":{"name": "王"}}
                ],
                "filter" : {
                    "range" : {
                        "age":{
                            "gte":50,
                            "lte": 60
                        }
                    }
                }
            }
        }
    }
    以上查询在 filter 中使用 range 来限定年龄的值区间在 50(不包含) 到 60(包含) 最之间
    