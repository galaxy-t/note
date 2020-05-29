# Netty


## 核心组件

### Bootstrap ServerBootstrap

    Bootstrap 意思是引导程序的意思 , 一个 Netty 应用通常由一个 Bootstrap 开始 , 主要作用是配置整个 Netty 程序 , 串联各个组件 , 
    Netty 中 Bootstrap 类是客户端程序的启动引导类 , ServerBootstrap 是服务端启动引导类 .
    
### Future

    Future 提供了另外一种在操作完成时通知应用程序的方式 . 这个对象可以看作一个异步操作的结果占位符 . 通俗来讲 , 它相当于一位指挥官 , 
    发送了一个请求 , 建立完链接 , 通信完毕 , 你通知一声它会来关闭各项 IO 通道 , 整个过程 , 它是不阻塞的 , 是异步的 .
    在 Netty 中所有的 IO 操作都是异步的 , 不能理科得知消息是否被正确处理 , 但是可以过一会等它执行完成或者直接注册一个监听 , 
    具体的实现就是通过 Future 和 ChannelFuture , 它们可以注册一个监听 , 当操作执行成功或失败时监听会自动出发注册的监听事件 .
    
### Channel 
    
    Channel 类似 Socket , 它代表一个实体 (如一个硬件设备、一个网络套接字) 的开放连接 , 如读写操作 . 
    通俗地讲 , Channel字面意思就是通道 , 每一个客户端与服务端之间进行通信的一个双向通道 .
    
    
https://crossoverjie.top/categories/%E7%AE%97%E6%B3%95/Java/Netty/      博客

https://www.bilibili.com/video/av95343210/  视频