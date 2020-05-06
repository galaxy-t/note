# Docker

    Docker 是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的镜像中，
    然后发布到任何流行的 Linux或Windows 机器上，也可以实现虚拟化。容器是完全使用沙箱机制，相互之间不会有任何接口。
    
    本文主要介绍如何安装，使用 Docker ，及以 SpringBoot 项目为例，讲解如何使用 Maven 一键发不到 Docker 运行。
    
### 准备
    
1. 安装 JDK8 ，JDK 在 linux 上的安装本文不做讲解，本次安装使用的是 JDK1.8.0_221
    
    > 使用 java -version 命令查看当前 JDK 版本

2. 检查 Linux 内核( Docker 需要64位版本，同时内核版本在3.10以上，如果版本低于3.10，需要升级内核)

    > 使用 uname -r 命令查看当前 Linux 内核版本
    
### 安装

    从 2017 年 3 月开始 docker 在原来的基础上分为两个分支版本: Docker CE 和 Docker EE。
    Docker CE 即社区免费版，Docker EE 即企业版，强调安全，但需付费使用。
    本文介绍 Docker CE 的安装使用。
      
1. 移除旧的版本

    > yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-selinux \
                  docker-engine-selinux \
                  docker-engine
                      
2. 安装一些必要的系统工具

    > yum install -y yum-utils device-mapper-persistent-data lvm2
    
3. 添加软件源信息(使用阿里的镜像资源)

    > yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
    
4. 更新 yum 缓存

    > yum makecache fast
    
5. 安装 Docker-ce

    > yum -y install docker-ce
    
6. 启动 Docker 后台服务

    > systemctl start docker
    
7. 测试运行 hello-world

    > docker run hello-world
    
    > 由于本地没有hello-world这个镜像，所以会下载一个hello-world的镜像，并在容器内运行。
    
8. 配置 Docker 远程访问

    > 编辑文件 vim /usr/lib/systemd/system/docker.service
    
    > 将 ExecStart 修改为 ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock
    
    > 通知docker服务做出的修改 systemctl daemon-reload
    
    > 重启 service docker restart（至今我还是喜欢使用这种启停服务的命令）
    
    > 浏览器访问 http://125.35.86.214:2375/version 如果有返回数据则配置成功
    
### 使用 MAVEN 进行发布

1. 配置 MAVEN 环境变量

    > 添加环境变量 M2_HOME   D:\java\tools\apache-maven-3.6.1
    
    > PATH 追加 %M2_HOME%\bin
    
2. 配置 Docker 环境变量 

    > DOCKER_HOST     tcp://192.168.0.200:2375
    
3. 使用 MAVEN 命令将项目打包发布到 Docker

    > mvn clean package dockerfile:build -DskipTests
    
    
### 常用命令 

1. 服务

    > 启动docker服务
      service docker start
      
    > 查看全部镜像
      docker images
      
    > 查看正在运行得容器列表
      docker ps
      
    > 查看运行过得容器列表
      docker ps -a
      
2. 镜像

    > 拉取镜像（拉取 ubuntu 的 13.10 版本的镜像）
      docker pull ubuntu:13.10

    > 查看全部镜像
      docker images
      
    > 删除一个镜像，执行删除之前需要确定该镜像没有容器在运行
      docker rmi 镜像名/镜像id
      
    > 强制删除一个镜像
      docker rmi -f 镜像名/镜像id
      
3. 容器

    > 查看正在运行的容器列表
      docker ps
    
    > 查看运行过的容器列表
      docker ps -a
      
    > 已容器的名字启动一个已有容器
      docker start register
      
    > 停止容器 
      docker stop register
      
    > 删除一个容器
      docker rm register
      
4. 运行

    -d : 让容器在后台运行
    -p : 将容器内部使用的网络端口映射到我们使用的主机上
    -v : 将容器的某个文件夹映射到宿主机的某个文件夹中
    --name : 用于为一个容器命名
    8081:8080  8081为宿主机的端口  8080为Docker容器的端口，意为将 Docker 容器的8080端口映射到宿主机的8081端口上
    --privileged=true : 允许开启特权功能。
        使用该参数，container内的root拥有真正的root权限。
        否则，container内的root只是外部物理机的一个普通用户权限。
        使用privileged启动的容器，可以看到很多host上的设备，并且可以执行mount。
        甚至允许你在docker容器中启动docker容器。不启用privileged，容器中root用户不能执行mount。
        
    --net=host ： 如果启动容器的时候使用 host 模式，那么这个容器将不会获得一个独立的 Network Namespace，而是和宿主机共用一个 Network Namespace。容器将不会虚拟出自己的网卡，配置自己的 IP 等，而是使用宿主机的 IP 和端口。
        例如容器内部有一个 WEB 应用使用的 8080 端口，直接运行容器，外部访问这个应用的时候直接使用 宿主机的 IP地址加 8080 端口即可，不需要再单独进行配置，此种方式在小规模部署及测试时使用是最值得推荐的

    > 首次启动一个镜像
      docker run -d -p 8080:8080 ceti
      
    > 首次启动一个镜像并命名（首次启动镜像 a ，并将这个容器命名为 b）
      docker run -d -p 8889:8889 --name ceti_01 ceti
      
    > 首次启动一个镜像并与宿主机共享网络和文件夹，且具有root权限，共享网络得情况下不需要再进行指定端口
      （将容器的 /logs 文件夹映射到宿主机的 /logs/user 文件夹）
      docker run -d -v /logs/user:/logs --privileged=true --net=host --name ceti_01 ceti
      
    > 实时查看Docker容器内部的标准输出
      docker logs -f 容器名  
      
    > 实时查看容器日志
      docker logs -f -t --tail 行数 容器名
      
    > 查看容器ip地址
      docker inspect test|grep -i add
    
5. 项目打包发布

    > MAVEN打包发布DOCKER镜像命令
      mvn clean package dockerfile:build -DskipTests
    

### 导入导出镜像

1. 导出镜像, -o 为导出的文件重命名,必须为 tar 格式, 最后的 redis 是镜像的名字,最终 redis.tar 会保存到当前目录下
    
    > docker save -o redis.tar redis
                                                           
2. 导入镜像

    > docker load < redis.tar
                                                         
### 导入导出容器       

1. 导出容器, -o 为导出的文件重命名,必须为 tar 格式, 最后的 mysql8 是容器的名字,最终 mysql8.tar 会保存到当前目录下

    >   docker export -o mysql8.tar mysql8

2. 导入容器