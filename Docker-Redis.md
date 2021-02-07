


1. 下载镜像(可以指定版本号)

    > docker pull redis:5.0.7
    
2. 创建一个文件夹，以用于存放 redis 的数据

    > mkdir /usr/local/redis
    
3. 使用 redis 镜像运行 redis 容器

    > docker run -d -p 6379:6379 -v /usr/local/redis:/data --restart=always --name redis redis:5.0.7 --appendonly yes --requirepass "123456"
    > -d : 后台运行
    > -p : 宿主机端口与容器端口映射，也可以使用 host 模式
    > -v : 挂在：将容器中的redis持久化数据挂在到宿主机，避免容器重启导致数据的丢失
    > --restart=always : 无论什么情况挂机，总是重启
    > --name : 容器名称
    > redis:5.0.7 : 指定使用的镜像
    > --appendonly yes：redis 运行时开启持久化
    > --requirepass "123456"：设置 redis 登陆密码    
    
    > docker run -d  -v /usr/local/redis:/data --privileged=true --net=host --restart=always --name redis redis:5.0.7 --appendonly yes --requirepass "123456"
    > host 模式
    
    
    
    
    