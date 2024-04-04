# Netty

    是一个异步事件驱动的框架, 用于快速开发高性能的服务端和客户端
    封装了 JDK 底层的 BIO和NIO 模型, 提供了高度可用的 API
    自带编解码器, 解决拆包粘包问题, 用户只需要关心业务逻辑
    精心设计的 Reactor 线程模型, 支持高并发海量连接
    自带协议栈, 无需用户关心
    
    
    1. NIOEventLoop: 事件循环
    2. Channel: 连接
    3. ByteBuf: 传递数据
    4. ChannelHandler: 业务逻辑处理
    5. Pipeline: 用于贯穿 ChannelHandler

## API

### Buffer

    针对 java.nio 中的 ByteBuffer 进行了封装, java.nio 的 Buffer 类实现的功能有限且没有优化过. 
    Netty 的缓冲区叫做 ByteBuf, 实际上等价于 java.nio 中的 ByteBuffer, 从根本上解决了 JDK 缓冲区的问题.
    因为缓冲区传送数据都是通过 Netty 的 ChannelPipeline和ChannelHandler, 所以 Netty 应用都会使用到缓冲区的 API

#### ByteBuf

    其拥有两个指针, 分别是 读指针和写指针, 读指针永远小于等于写指针, 所以读不允许读取未写入的数据
    常见的 ByteBuf 有三种, 其它的也有不少, 不过一般开发者很少会用到, 都是 Netty 内部使用, 自己也可以实现自己的缓冲区.

##### 堆缓冲区

    最常见的 ByteBuf 是存储数据在 JVM 堆内存上的缓冲区, 这种缓冲区内部实现就是一个数组

    // 创建一个堆缓冲
    ByteBuf heapBuf = null;

    // 检查其是否有数组, 非堆缓冲是没有数组的, 所以可以通过该方法来判断其是否为堆缓冲
    if (heapBuf.hasArray()) {

        // 获取其数组引用
        byte[] array = heapBuf.array();

        // 计算第一个字节所在位置
        int offset = heapBuf.arrayOffset();

        // 获取可读字节数
        int length = heapBuf.readableBytes();

        // 使用自己的业务逻辑处理数据
        YourImpl.method(array, offset, length);
    }

##### 直接缓冲区

    直接缓冲区的意思是这个缓冲区分配的内存实在 JVM 堆内存外部, 使用直接内存时你不会在 JVM 堆空间中看见它的使用量,
    所以在计算应用内存使用量时, 直接内存区域也要计算上, 然后限制它的最大使用量, 防止系统内存不够出现错误.
    通过网络传输数据时直接缓冲区性能是很高的.

        ByteBuf directBuf = null;

        // 检查缓冲区是否有数组, 返回 false 就是直接缓冲区
        if (!directBuf.hasArray()) {
            
            // 获取可读字节数量
            int length = directBuf.readableBytes();
            // 初始化相同长度的直接数组
            byte[] array = new byte[length];
            // 读取数据到数组中
            directBuf.getBytes(array);
            // 使用自己的业务逻辑处理数据
            YourImpl.method(array, 0, array.length);
        }

##### 组合缓冲区(CompositeByteBuf)

    它允许将不同类型的 ByteBuf 组合在一起并且提供访问他们的方式, CompositeByteBuf 也像是各种类型 ByteBuf 的视图,
    它的 hasArray() 方法返回的 false, 因为它里面可能包含多个堆缓冲或直接缓冲.

    CompositeByteBuf compBuf = null;

    ByteBuf heapBuf = null;
    ByteBuf directBuf = null;

    // 将数据都放入到 CompositeByteBuf
    compBuf.addComponents(heapBuf, directBuf);

    // 移除索引是 0 的数据, 就像 list
    compBuf.removeComponent(0);
    // 便利组合缓冲区中的缓冲实例
    for (ByteBuf buf : compBuf) {
        System.out.println(buf.toString());
    }

    // 检查是否有数组, CompositeBuf 会返回 false
    if (!compBuf.hasArray()) {

        // 获取可读字节总数
        int length = compBuf.readableBytes();
        // 初始化相同长度的直接数组
        byte[] array = new byte[length];
        // 读取数据到数组中
        compBuf.getBytes(array);
        // 使用自己的业务逻辑处理数据
        YourImpl.method(array, 0, array.length);
    }


#### ByteBufHolder

    使用 ByteBufHolder 时 Netty 可以优化分配 ByteBuf, 例如使用池化技术, 也能够自动释放这部分资源

    content() 返回它存储的内容的 ByteBuf
    copy() 返回一个深拷贝的 ByteBufHolder, 也就是他们的数据不共享

#### ByteBufAllocator

    ByteBuf 分配器

#### Unpooled

    非池化 buf 分配器
    在非 Netty 项目中可以直接使用

#### ByteBufUtil

    提供了很多静态方法直接操作 ByteBuf, 相比较上面的 Unpooled, 这个类提供的方法是比较通用的并且不需要开发人员关心 ByteBuf 是否使用了池技术.
    





### Channel

    实际上就是一个 Socket

### ChannelHandler

    用来处理各种 Channel 对应的事件的, 如读写等等


### ChannelHandlerContext

### ChannelPipeline

    流水线或容器

### NIOEventLoopGroup

## 注意

### 当手机开启了飞行模式 , 或手机强制关机了 , Socket 是识别不到这些情况的 , 所以需要建立心跳机制

