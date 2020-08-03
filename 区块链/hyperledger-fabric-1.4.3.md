# Hyperledger fabric 笔记

    
## 安装

    以下安装均以 Hyperledger fabric 1.4.3 版本为准
    系统: CentOS
    
### 准备

    1. 检查是否安装 Docker (具体安装详见 Docker.md)

    2. 检查是否安装 Docker-compose (具体安装详见 Docker-compose.md)

    3. 下载
       需要下载以下四个文件:
       1: fabric 源码 , 地址 https://github.com/hyperledger/fabric , 得到 fabric-1.4.3.zip
       2: fabric-samples 源码 , 地址 https://github.com/hyperledger/fabric-samples , 得到 fabric-samples-1.4.3.zip
       3: fabric 编译后的二进制文件 , 地址 https://github.com/hyperledger/fabric/releases/tag/v1.4.3 , 得到 hyperledger-fabric-linux-amd64-1.4.3.tar.gz
          其实通过源码自己编译也可以 , 但是我不会 , 而且看教程也挺麻烦的 , github 上已经提供 , 直接下载即可 
       4: fabric-ca 编译后的二进制文件 , 地址 https://github.com/hyperledger/fabric-ca/releases/tag/v1.4.3 得到 hyperledger-fabric-ca-linux-amd64-1.4.3.tar.gz
       注: 以上在 github 下载的时候需要注意选择 v1.4.3 分支和 tag
    
    4. 解压
    
        1: 首先解压  fabric-1.4.3.zip 和 fabric-samples-1.4.3.zip , 得到 fabric-1.4.3 和 fabric-samples-1.4.3
        2: 然后将使用压缩工具打开 hyperledger-fabric-linux-amd64-1.4.3.tar.gz , 里面有 bin 和 config 两个文件夹 , 将他们解压出来
        3: 使用压缩工具打开 hyperledger-fabric-ca-linux-amd64-1.4.3.tar.gz , 里面只有一个 bin 文件夹 , bin 文件夹中只有一个 fabric-ca-client 文件 , 将这个文件解压出来放到第二步解压出来的 bin 目录中
        4: 然后将第三步的 bin 和 config 文件夹剪切(或粘贴)到 fabric-samples-1.4.3 的根目录下
        
    5. 上传
    
        将 fabric-1.4.3 和 fabric-samples-1.4.3 通过 ftp 上传到服务器上去 , 建议放在 /usr/local 目录下
        我的习惯是在 /usr/local 目录下创建一个 hyperledger 文件夹 , 然后将 fabric-1.4.3 和 fabric-samples-1.4.3 两个文件放到该目录下
        此时我们的路径为
        /usr/local/hyperledger/
                                fabric-1.4.3
                                fabric-samples-1.4.3
### 安装
    
    其实说是安装 , 倒不如说是通过一些脚本拉取一些 docker 镜像等等
    
    进入到 /urs/local/hyperledger/fabric/scripts 目录下 , 有一个 bootstrap.sh 可执行文件
    此处先解释一下执行这个文件会发生些什么 , 打开它然后看最下面的代码
       
    if [ "$SAMPLES" == "true" ]; then
      echo
      echo "Installing hyperledger/fabric-samples repo"
      echo
      samplesInstall
    fi
    if [ "$BINARIES" == "true" ]; then
      echo
      echo "Installing Hyperledger Fabric binaries"
      echo
      binariesInstall
    fi
    if [ "$DOCKER" == "true" ]; then
      echo
      echo "Installing Hyperledger Fabric docker images"
      echo
      dockerInstall
    fi
    以上代码按照顺序来读 , 其实就干了三件事情
    1: 下载 fabric-samples 源码 , 其实就是讲 fabric-samples 的源码克隆到本地 , 这样就会要求我们首先在服务器上安装 git , 否则 git 的克隆命令就不能用
       上面的准备工作中我们已经手动下载了 fabric-samples 源码并解压上传 , 所以这一步不需要脚本帮我们执行 , 把这段 if 判断的代码删掉也好 , 把文件中 SAMPLES=true 改为 SAMPLES=false 也好 , 只要别让他执行就行了
    2: 下载可执行文件 , 其实下载的就是 fabric 编译之后的二进制文件 , 也就是在准备工作中操作过的 bin 和 config , 当然也包含 fabric-ca 中的 bin , 我们也已经手动下载,解压,合并以及拷贝到 fabric-samples 中了,
       这一步简直就是丧心病狂 , 因为那个下载地址根本就打不开 , 翻墙也打不开 , 所以只能自己去 github 上下载了
       所以这一步我们也学第一步无论是删掉这段判断还是修改 BINARIES 变量为 false 反正是不能让他执行 , 否则直接会报错然后中断
    3: 下载 Docker 镜像 , 最有用的其实就是这一步了 , 我们本地的 Docker 用了 阿里的镜像加速器 , 所以不用担心速度
    
    好了解释完了直接执行 ./bootstrap.sh 
    最后执行完就能看到你拉取下来的镜像了
    至此 , 所有要下载的或者要安装都工作都已经结束
    
### 测试
    
    1. 启动网络
       cd /usr/local/hyperledger/fabric-samples/first-network
       ./byfn.sh up
       启动成功可以通过 docker ps 命令查看节点的启动情况
       
    2. 关闭网络
       cd /usr/local/hyperledger/fabric-samples/first-network
       ./byfn.sh down
       输入 Y 
       
## 网络

    在 /usr/local/hyperledger/fabric-samples/first-network 路径下有一个 byfn.sh 脚本 , 就是我们在测试环节的第一步中用到的那个 , 那么执行 ./byfn.sh up 脚本都做了那些事情呢?
    
    1. 该命令利用已经构件的 Docker 镜像快速启动网络 , 首先该脚本会启动一个 orderer 节点和四个归属于两个不同组织的 peer 节点, 还将启动一个容器运行脚本 , 它将 peer 节点加入到通道(Channel),部署和实例化链码 , 
       并根据已部署的链码驱动交易执行 .  
       ./byfn.sh up 可使用的参数
            -c：设置通道名称，默认为 mychannel，例：./byfn.sh up -c testchannel
            -t：设置 CLI 超时参数，不设置的情况下 CLI 将放弃 10 秒后发出查询请求的默认设置
            -l：设置智能合约的语言，默认是 go，例：./byfn.sh up -l java
            -o：设置排序服务方式，默认是 solo，例：./byfn.sh up -o kafka  
    2. 其实执行 ./byfn.sh up 之后 , 该脚本会启动 一个 orderer 节点 , 四个 peer 节点 , 一个 cli 容器 , 其余的工作实际是在 cli 容器中执行 /usr/local/hyperledger/fabric-samples/first-network/scripts/script.sh 脚本
       说白了其实 cli 容器就是一个客户端 , 只不过是命令行客户端 , 运行在 Docker 容器中 , 默认情况下 , cli 的身份是 admin.org1 , 链接 peer0.org1 节点
       以下对 script.sh 脚本进行简单的描述
       1: 创建通道 , admin.org1 连接 peer0.org1 节点 , 向 orderer 节点传递要创建的通道名称(默认为 mychannel)和通道配置交易 channel.tx . 如果创建成功 , 则返回通道的创世区块 CHANNEL_NAME.block , 该区块包含 channel.tx 指定的
          通道配置信息 , 保存在 cli 容器中.
       2: 加入通道 , 循环加入两个组织的两个 peer , 然后设置 cli 容器的环境变量来修改 cli 的身份 , 链接不同的节点 , 使用 peer channel join 命令让节点加入通道 , 第一步中 orderer 节点创建通道成功后返回的 CHANNEL_NAME.block 文件
          存储在 cli 容器中 , 在设置完环境变量切换到不同节点身份之后使用 peer channel join -b $CHANNEL_NAME.block >&log.txt 命令使当前节点加入到通道中 , 节点加入通道后会创建以 CHANNEL_NAME.block 开头的链
       3: 更新锚节点
          一个组织只能有一个锚节点 , 节点加入通道后才能进行更新   
       4: 安装链码
          安装链码需要制定链码的配置信息 peer chaincode install -n mycc -v ${VERSION} -l ${LANGUAGE} -p ${CC_SRC_PATH} >&log.txt , 
          -n 是链码的名称(mycc)；-v 是版本号；-l 是指定链码的语言，如果不使用该标志默认使用 go 语言；-p 是指向链码路径，默认是 github.com/chaincode/chaincode_example02/go/
       5: 实例化链码
          执行命令 peer chaincode instantiate -o orderer.example.com:7050 -C $CHANNEL_NAME -n mycc -l ${LANGUAGE} -v ${VERSION} -c '{"Args":["init","a","100","b","200"]}' -P "AND ('Org1MSP.peer','Org2MSP.peer')" >&log.txt
          给 orderer 发送命令 , 要求在 CHANNEL_NAME 通道上 部署名称为 mycc 的链码 , -l 设置语言 , -v 设置链码的版本 , -c 为参数 , -P 指定背书策略必须为 org1 和 org2 共同背书 , 即从 org1 和 org2 中各有一个节点组合背书
       6: 调用链码 
          执行命令 peer chaincode invoke -o orderer.example.com:7050 -C $CHANNEL_NAME -n mycc $PEER_CONN_PARMS -c '{"Args":["invoke","a","b","10"]}' >&log.txt
       7: 查询链码
          执行命令 peer chaincode query -C $CHANNEL_NAME -n mycc -c '{"Args":["query","a"]}' >&log.txt
    3. 关闭网络 ./byfn.sh down
       该命令会将证书材料,组件及容器等都删除
       
## 链码

    只能合约在 Hyperledger Fabric 中称为链码(chaincode) , 用于操作分布式账本 . 链码被部署在 fabric 的网络节点中 , 能够独立运行在具有安全特性的受保护的 Docker 容器中 , 以 gRPC 协议与相应的 peer 节点进行通信 , 以操作分布式账本中的数据 .
    一般链码分为两种: 系统链码 和 用户链码
    
### 系统链码

    负责 Fabric 节点自身的逻辑处理 , 包括 系统配置,背书,校验 等工作 , 在 Peer 节点启动时回自动完成注册和部署.
    1: 配置系统链码(Configuration System Chaincode，CSCC) : 负责处理 Peer 端的 Channel 配置
    2: 生命周期系统链码(Lifecycle System Chaincode，LSCC) : 负责对用户链码的生命周期进行管理
    3: 查询系统链码（Query System Chaincode，QSCC） : 提供账本查询 API . 如获取区块和交易等信息
    4: 背书管理系统链码（Endorsement System Chaincode，ESCC） : 负责背书(签名)过程 , 并可以支持对背书策略进行管理
    5: 验证系统链码（Validation System Chaincode，VSCC） : 处理交易的验证 , 包括检查背书策略以及多版本并发控制
    
### 用户链码

    用户链码不同于系统链码 , 系统链码是 fabric 的内置链码 , 而用户链码是由应用程序开发人员根据不同场景需求编写的基于分布式账本的状态的业务处理逻辑代码 , 
    运行在链码容器中 , 通过 Fabric 提供的接口与账本状态进行交互 . 
    用户链码向下可对账本数据进行操作 , 向上可以给企业及应用程序提供调用接口
    
### 链码的生命周期

    管理 Chaincode 的生命周期共有五个命令
    
    1. install : 将已编写完成的链码安装在网络节点中
    2. instantiate : 对已安装的链码进行实例化
    3. upgrade : 对已有链码进行升级 , 链代码可以在安装后根据具体需求的变化进行升级
    4. package : 对指定的链码进行打包操作
    5. singnpackage : 对已打包的文件进行签名
    
#### 链码的编写

    package main
    
    import (
    	"encoding/json"
    	"fmt"
    	"github.com/hyperledger/fabric/core/chaincode/shim"
    	"github.com/hyperledger/fabric/protos/peer"
    )
    
    /**
    定义链码对象
     */
    type Chaincode struct {
    }
    
    /**
    定义一个苹果的对象
    */
    type apple struct {
    	No    string //苹果的编号
    	Color string //苹果的颜色
    	Size  int    //苹果的尺寸
    }
    
    // 初始化函数
    // 为结构体添加 Init 方法
    // 在该方法中实现链码初始化或升级时的处理逻辑
    // 编写时可灵活使用 stub 中的 API
    func (t *Chaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
    
    	// 新增一个苹果
    	appleJson, err :=  json.Marshal(apple{No: "0", Color: "红色", Size: 12})
    	//若转换 json 出现异常
    	if err != nil {
    		return shim.Error(err.Error())
    	}
    	stub.PutState("0", appleJson)
    
    
    	return shim.Success(nil)
    }
    
    func (t *Chaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
    	// 解析用户调用链码传递的函数名及参数
    	fun, args := stub.GetFunctionAndParameters()
    	if fun == "queryApple" {
    		return t.queryApple(stub, args)
    	}
    	return shim.Success(nil)
    }
    
    func (t *Chaincode) queryApple(stub shim.ChaincodeStubInterface,args[] string) peer.Response {
    	appleAsBytes,_ := stub.GetState(args[0])
    	return shim.Success(appleAsBytes)
    }
    
    // 主函数
    // 其中调用 shim.Start() 方法
    func main() {
    	// 启动
    	err := shim.Start(new(Chaincode))
    	if err != nil {
    		fmt.Printf("Chaincode start error [%s]", err)
    	}
    }
    
    以上为一份简单的链码程序 , 链码启动必须通过调用 shim 包中的 Start 函数 , 传递一个类型为 Chaincode 的参数 , 其实这么说也不完全 , 只要该参数实现两个接口即可 , 
    Init 和 Invoke 两个方法
    Init：在链码实例化或升级时被调用, 完成初始化数据的工作
    Invoke：更新或查询帐本数据状态时被调用， 需要在此方法中实现响应调用或查询的业务逻辑
    