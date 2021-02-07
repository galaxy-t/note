# Hyperledger fabric 笔记

## 安装

    以下安装均以 Hyperledger fabric 2.1.1 版本为准
    系统: CentOS
    
### 准备

    1. 检查是否安装 Docker (具体安装详见 Docker.md)

    2. 检查是否安装 Docker-compose (具体安装详见 Docker-compose.md)

    3. 下载
       需要下载以下四个文件:
       1: fabric 源码 , 地址 https://github.com/hyperledger/fabric , 得到 fabric-2.1.1.zip
       2: fabric-samples 源码 , 地址 https://github.com/hyperledger/fabric-samples , 得到 fabric-samples-2.1.1.zip
       3: fabric 编译后的二进制文件 , 地址 https://github.com/hyperledger/fabric/releases/tag/v2.1.1 , 得到 hyperledger-fabric-linux-amd64-2.1.1.tar.gz
          其实通过源码自己编译也可以 , 但是我不会 , 而且看教程也挺麻烦的 , github 上已经提供 , 直接下载即可 
    
    4. 解压
    
        1: 首先解压 fabric-samples-2.1.1.zip , 得到 fabric-samples-2.1.1
        2: 然后将使用压缩工具打开 hyperledger-fabric-linux-amd64-2.1.1.tar.gz , 将里面的 bin 和 config 文件夹解压到 fabric-samples-2.1.1 的根目录下
        3: 使用压缩工具打开  fabric-2.1.1.zip 里面有一个 scripts 的文件夹 , 其中有一个 bootstrap.sh 的可执行文件 , 将其解压出来
        
    5. 上传
    
        将 fabric-samples-2.1.1 和 bootstrap.sh 通过 ftp 上传到服务器上去 , 建议放在 /usr/local 目录下
        我的习惯是在 /usr/local 目录下创建一个 hyperledger 文件夹 , 然后将 fabric-samples-2.1.1 和 bootstrap.sh 放到该目录下
        此时我们的路径为
        /usr/local/hyperledger/
                                bootstrap.sh
                                fabric-samples-1.4.3
### 安装
    
    其实说是安装 , 倒不如说是通过一些脚本拉取一些 docker 镜像等等
    
    打开 bootstrap.sh 可执行文件
    此处先解释一下执行这个文件会发生些什么 , 什么都不用看 , 直接翻到最后
       
    if [ "$SAMPLES" == "true" ]; then
        echo
        echo "Clone hyperledger/fabric-samples repo"
        echo
        cloneSamplesRepo
    fi
    if [ "$BINARIES" == "true" ]; then
        echo
        echo "Pull Hyperledger Fabric binaries"
        echo
        pullBinaries
    fi
    if [ "$DOCKER" == "true" ]; then
        echo
        echo "Pull Hyperledger Fabric docker images"
        echo
        pullDockerImages
    fi
    
    似曾相识的代码 , 在 1.4.3 笔记中讲述了这一段代码 , 其实现在真正有用的就是拉取 docker 镜像这一步了 , 自行找到 $SAMPLES 和 $BINARIES 两个变量 , 
    将其修改为 false
    
    好了解释完了直接执行 ./bootstrap.sh 
    最后执行完就能看到你拉取下来的镜像了
    至此 , 所有要下载的或者要安装都工作都已经结束
    
### 测试
    
    1. 启动网络
       cd /usr/local/hyperledger/fabric-samples/test-network
       ./network.sh up
       启动成功可以通过 docker ps 命令查看节点的启动情况
       
    2. 关闭网络
       cd /usr/local/hyperledger/fabric-samples/test-network
       ./network.sh down
       
    3. 创建通道
    默认执行完第一步该脚本只是创建了一个区块链网络 , 该网络中有 org1 和 org2 两个组织 , 每个组织都有一个 peer0 节点 , 另外还有一个 order 容器在执行
    此时需要我们手动自行创建一个 channel 
    ./network.sh createChannel -c mycc
    -c 用于指定创建的 channel 的名称为 mycc , 这个可以自行修改一下
    执行 docker exec -it bd23c1447edf peer channel list 来查看一下我们刚才创建的通道 , bd23c1447edf 为 某个组织的某个节点的 docker 容器的 ID , 因为 2.* 版本以后不提供 cli 容器 , 而且启动的容器都不提供 bash 所以我们只能如此执行
    
    此处解释一下执行完 ./network.sh createChannel -c mycc 命令这个脚本做了些什么
    1: network.sh 中有一个 createChannel() 方法 , 其中提供了检查网络是否已经启动等等 , 当然最重要的是其中的 scripts/createChannel.sh $CHANNEL_NAME $CLI_DELAY $MAX_RETRY $VERBOSE  这一行
       其执行了当前目录下的 scripts 目录下的 createChannel.sh 这个脚本 , 并传递了几个参数
       $CHANNEL_NAME   要创建的 channel 的名称 , 缺省为 : mychannel
       $CLI_DELAY      这个参数从字面意义上来看应该是执行等待时间 , 其实进入 createChannel.sh 中找到 createChannel() 方法就会看到 , 其在正式创建通道的时候是个循环 , 每次都会等待几秒用来等待创世区块的创建
       $MAX_RETRY      最大等待时间 , 一共等待多久
       $VERBOSE         这个是打印详细日志的意思?
       接下来让我们来到 scripts/createChannel.sh 脚本
    2: createChannelTx() 该脚本的第一步是执行 createChannelTx() 方法
       createChannelTx() 方法用于创建 ChannelTx , 具体 ChannelTx 是个什么东西现在我还没有找到明确的答案 , 现在先预先猜测其应该为要创建的通道的配置文件
       configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/${orgmsp}anchors.tx -channelID $CHANNEL_NAME -asOrg ${orgmsp}
       主要是通过以上这一句来生成 ChannelTx 文件的 , 生成的该文件会以要创建的 channel 名称加 .tx 为后缀放在 /root/hyperledger/fabric-samples-2.1.1/test-network/channel-artifacts 目录下
       如本次生成的 tx 文件名称为 mycc.tx 
    3: createAncorPeerTx() 该脚本第二部是执行 createAncorPeerTx() 方法
       createAncorPeerTx() 方法用于创建锚节点的 tx 文件
       首先该方法定义了两个名字 Org1MSP Org2MSP , 然后循环为他们创建 tx 文件
       configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/${orgmsp}anchors.tx -channelID $CHANNEL_NAME -asOrg ${orgmsp}
       通过上述命令来创建锚节点的 tx 文件
       这样会生成两个 tx 文件 Org1MSPanchors.tx  Org2MSPanchors.tx , 这两个文件应该配置了当前网络中的两个组织的信息和当前要创建的 channel 的信息 , 具体他们怎么配置进去的 , 我特么哪知道
    4: createChannel() 该脚本第三步执行该方法 , 用于创建通道和生成创世区块
       方法内部首先设置环境变量 , 设置的环境变量是为当前宿主机设置的 , 具体设置到哪个节点的环境变量信息 , 后面执行的 peer 命令就是在哪个节点中的 docker 容器中执行的
       peer channel create -o localhost:7050 -c $CHANNEL_NAME --ordererTLSHostnameOverride orderer.example.com -f ./channel-artifacts/${CHANNEL_NAME}.tx --outputBlock ./channel-artifacts/${CHANNEL_NAME}.block --tls --cafile $ORDERER_CA >&log.txt
       上面这句话用来在具体的 peer 节点上创建 channel -o 指定了 orderer 的地址和端口号 -c 指定了要创建的通道的名称 -f 指定了刚才生成的 mycc.tx 所在的目录 --outputBlock 指定了该命令创建的创世区块的名称和存放的位置 , 其它的命令自己猜吧
    5: joinChannel() 将制定的组织加入到刚创建的通道当中
       该方法中也是先设置环境变量 , 类似于上一步那样子
       peer channel join -b ./channel-artifacts/$CHANNEL_NAME.block >&log.txt
       以上 -b 指定了创世区块的位置
    6: updateAnchorPeers() 用于更新每个组织的锚节点
       peer channel update -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com -c $CHANNEL_NAME -f ./channel-artifacts/${CORE_PEER_LOCALMSPID}anchors.tx --tls --cafile $ORDERER_CA >&log.txt
       以上 -o 指定了 orderer 的地址和端口 , -c 指定了 channel 的名称 , -f 指定了组织锚节点的 tx 文件 其它的命令就可以自己猜了
    经过以上步骤通道已经创建完成 , 并且该测试网络的全部组织和节点都已经加入到该 channel 中
       
## 网络

        