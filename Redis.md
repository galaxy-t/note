# Redis


### Windows 安装使用

       https://github.com/microsoftarchive/redis/releases
       
       下载 Redis-x64-3.0.504.zip 并解压 
       Dos 进入该解压目录
       执行命令 redis-server.exe redis.windows.conf 启动 redis , 后面的 redis.windows.conf 可以省略 , 如果省略 , 会启用默认的
       执行之后的界面不要关闭
       
       测试
       redis-cli.exe -h 127.0.0.1 -p 6379   使用该命令登录本地命令行客户端
       
       
       
### 设置密码

    CONFIG get requirepass  通过该命令查看是否设置了密码
    CONFIG set requirepass "123456"        设置密码为 123456
    设置密码后如果通过命令行连接成功后是操作不了 redis 的 , 需要执行 AUTH "123456" 来验证密码才能正常执行