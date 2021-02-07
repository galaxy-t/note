# Netty

    异步的
    事件驱动
    
    p37 结束
    
    
## WebSocket

    基于 HTTP 协议
    虽然是在 H5 基础上推出的 , 但是也可以在非浏览器的情况下使用 , 如: 安卓,IOS 
    WebSocket 如果需要在网页运行的话 , 必须要需要浏览器支持
    
## NIO
    
    在 java.nio 中有三个核心概念: Selector,Channel,Buffer . 在 Java.nio 中 , 我们是面向块 (block) 或者是面向缓冲区 (buffer) 编程的.
    Buffer 本身就是一块内存 , 底层实现上 , 它实际上是个数组 , 数据的 读,写 都是通过 Buffer 来实现的.
    
    除了数组之外 , Buffer 还提供了对于数据的结构化访问方式 , 并且可以追踪到系统的读写过程.
    
    Java 中的 8 种原生数据类型, 都有各自对应的 Buffer 类型, 如 IntBuffer,LongBuffer,CharBuffer 等.
    
    Channel 指的是可以向其写入数据或是从中读取数据的对象, 它类似于 java.io 中的 Stream.
    
    所有数据的读写都是通过 Buffer 来进行的, 永远不会出现直接向 Channel 写入数据的情况, 或是直接从 Channel 读取数据的情况.
    
    与 Stream 不同的是, Channel 是双向的, 一个流只可能是 InputStream或是OutputStream, Channel 打开后就可以进行读取,写入或者是读写.
    
    由于 Channel 是双向的, 因此它能更好的反映出底层操作系统的真实情况, 在 Linux 操作系统中, 底层操作系统的通道是双向的.
    
    属性: 
    capacity: Buffer 所能够包含的元素的数量, 即 Buffer 的容量(有几个格子), 其不可能为负数, 也永远不会变化
    limit: 是不应该读或写入的第一个元素的索引(从0开始), 如: 一个 Buffer 的 capacity 为 6 ,则其 6 个元素的索引为 0-5 , 
            而其第一个不能读也不能写的元素的索引为 6 , 但是其实并没有 6 这个位置 , 如果拿到按照数组的情况来看其应该会报数组越界
    position: Buffer 是下一个将要去读, 或将要去写的那个元素的索引, 其不可能为负数, 也不可能超过 limit , 默认开始的时候, 其应该是 0
    
    方法: 
    flip(): 将一个 Buffer准备好进行通道的写入和相对的读的操作, 在真正去读出或者写入的时候要先调用一下该方法 ,  
            其具体操作如下:
            假设现在有一个 capacity = 6 的 IntBuffer
            现在向里面放了 1,2,3,4 四个数字进去
            那么其属性应该为 capacity == 6,limit == 6,position==4 
            然后在执行完 flip() 方法之后: 
            1: limit=position , 将 position 的值赋值给 limit , 此时 limit = 4
            2: position=0 , 将 position 赋值为 0 , 此时 position 的结果与初始结果一致
    mark(): 做一个标记 , 即 mark = position , 当调用 reset() 方法时 , 读的位置会回到 mark() 标记到的位置 , mark 的值永远不会为负数且不会大于 position
    reset(): 重新将 position 定位到 mark() 设置的位置
    
    clear(): 将 Buffer 恢复到初始化的状态
    rewind(): 让一个 Buffer 准备重新再读取一次 , 其它属性不变 , position=0
    isReadOnly(): 并不是所有的 Buffer 都是可以进行写操作的 , 有的 Buffer 只允许进行读操作 , 称之为 只读Buffer , 
                  只读Buffer 只是针对其元素来说的 , 其中 limit和position等属性还是可以被修改的
                  通过 asReadOnlyBuffer() 方法可以得到一个只读 Buffer 
                  一个普通 Buffer 可以随时得到一个 只读Buffer , 但是一个 只读Buffer 是不可能得到一个 普通Buffer 的
    slice(): 根据设定好的 position和limit 得到一个新的 Buffer , 称之为 切面Buffer , 切面Buffer的值和原Buffer 的值共用一个数组
        
    Buffer 本身不是线程安全的, 如果需要并发操作, 需要自行进行同步操作
    
    0 <= mark <= position <= limit <= capacity
    
     
            
##注意

### 当手机开启了飞行模式 , 或手机强制关机了 , Socket 是识别不到这些情况的 , 所以需要建立心跳机制

https://crossoverjie.top/categories/%E7%AE%97%E6%B3%95/Java/Netty/      博客

https://www.bilibili.com/video/av95343210/  视频