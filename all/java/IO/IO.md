# IO

## 概念

I/O 是 Input/Output 的首字符缩写, 即输入/输出, 它描述的是数据流动的过程. 输入/输出是相对于内存而言的.

最常见的如: 磁盘IO(读写文件), 网络IO(网络请求和响应).

### 内核空间 和 用户空间

我们的应用程序是运行在用户空间的, 而只有内核空间才能进行系统态级别的资源有关的操作(如: 文件管理, 进程通信, 内存管理等).

也就是说, 我们想要进行 IO 操作, 就必须依赖内核空间的能力.

但是用户空间的程序(进程)是无法直接访问内核空间的.

此时我们就需要通过发起系统调用请求操作系统帮忙完成, 所以应用程序想要执行 IO 操作的话, 就必须通过调用内核提供的系统调用进行间接访问.

从应用程序的角度触发, 我们的应用程序对操作系统的内核发起 IO 调用(系统调用), 操作系统负责的内核执行具体的 IO 操作.
即强调的是通过向内核发起系统调用完成对 IO 的间接访问.

以上过程实际上是一次 IO 操作包含两个阶段:

1. IO 调用阶段: 应用程序进程向内核发起系统调用
2. IO 执行阶段: 内核执行 IO 操作并返回
    1. 内核等待 IO 设备准备好数据
    2. 内核将数据从内核空间拷贝到用户空间

### 缓冲区

缓冲区有内核缓冲区和用户进程缓冲区两种(多种?).

内核缓冲区是内核在内核空间开辟出来的一块缓冲空间, 而用户缓冲区是位于用户空间的一块缓冲空间.

缓冲区都是为了提升性能而出现的.

### IO 模型

同步和异步是通信机制, 阻塞和非阻塞是调用状态.

* 同步 IO: 用户线程发起 IO 请求后需要等待(或轮询)内核 IO 操作完成后才能继续执行.
* 异步 IO: 用户线程发起 IO 请求后可以继续执行, 当内核 IO 操作完成后会通知用户线程, 或调用用户线程注册的回调函数
* 阻塞 IO, 阻塞是指用户空间程序(具体线程)的执行状态, 用户空间程序需要等到 IO 操作彻底完成. 传统的 IO 模型都是同步阻塞
  IO, 在 Java 中默认创建的 socket 都是阻塞的.
* 非阻塞 IO, 用户程序不需要等待内核 IO 操作完成, 内核立即返回给用户一个状态值, 用户空间无需等到内核的 IO 操作彻底完成,
  可以立即返回用户空间, 执行用户的操作.

**注: 阻塞和非阻塞是针对同步来讲的, 异步实际上就是异步非阻塞, 异步没有阻塞这一说, 阻塞和非阻塞只是针对于同步来讲的**

#### BIO(Blocking IO)(同步阻塞 IO)

应用程序进程在发起 IO 调用到内核执行 IO 操作返回结果之前, 若发起系统调用的线程一直处于等待(阻塞)状态, 则此次 IO 操作为阻塞
IO.

当用户线程发起 IO 调用后, 内核回去查看数据是否就绪, 如果没有就会就会等待数据就绪, 而此时用户线程会处于阻塞状态, 用户线程交出
CPU(线程挂起),

当数据就绪之后, 内核会将数据拷贝到用户空间, 并返回结果给用户线程, 用户线程才会解除阻塞状态.

#### NIO(Non-Blocking IO)(同步非阻塞 IO)

应用程序在发起 IO 调用到内核执行 IO 操作返回结果之前, 若发起系统调用的线程不会等待而是立即返回, 则此次 IO 操作为非阻塞
IO.

当用户线程发起 IO 调用后, 并不需要等待内核处理, 而是内核立即返回一个结果, 如果结果是一个调用失败的信息(或其它状态),
此时代表数据还没有准备好,
接下来用户线程就可以再次(不断的)发送 IO 请求, 一旦内核数据准备好了, 并且又再次收到用户线程的请求, 那么内核就会马上将数据拷贝到用户线程,
然后返回.

在以上过程中, 用户线程需要不断地询问内核数据是否已经准备就绪, 即用户线程不会交出 CPU, 而会一直占用 CPU.

在 java.nio 中, 我们是面向块(block)或者是面向缓冲区(buffer)编程的

#### IO 多路复用模型(IO Multiplexing)

应用程序进程中会有一个 Selector 线程来不断的轮询内核, 应用进程中要进行 IO 操作的线程当作一个个的 Channel, 这些 Channel
会注册到 Selector 上并写明要关注的事件(接入, 读, 写等),
Selector 不断的轮询内核, 询问内核是否把注册过来的事件的数据准备好了, 如果准备好了 Selector 就会通知到具体的 Channel,
Channel 会发起 IO 调用(此次 IO 调用的过程(数据从内核空间 -> 用户空间)还是阻塞的).

目前 IO 多路复用的系统调用有 select, epoll 等等, select 系统调用目前所有的操作系统上都有支持.

* select: Reactor 模型, 内核提供的系统调用, 它支持一次查询多个系统调用的可用状态. 几乎所有的操作系统都支持. Linux操作系统的
  kernels 2.4内核版本之前, 默认使用select; 而目前windows下对同步IO的支持, 都是select模型.
* poll:  Reactor 模型, Linux下的JAVA NIO框架, Linux kernels 2.6内核版本之前使用poll进行支持.
* epoll: Reactor/Proactor 模型, Linux kernels 2.6内核版本及以后使用epoll进行支持；Linux kernels
  2.6内核版本之前使用poll进行支持；另外一定注意，由于Linux下没有Windows下的IOCP技术提供真正的 异步IO
  支持，所以Linux下使用epoll模拟异步IO
* kqueue: Proactor 模型, 目前JAVA的版本不支持

##### Channel(通道)

被建立的一个应用程序和操作系统交互事件、传递内容的渠道（注意是连接到操作系统）。一个通道会有一个专属的文件状态描述符。
那么既然是和操作系统进行内容的传递，那么说明应用程序可以通过通道读取数据，也可以通过通道向操作系统写数据。

所有要注册到 Selector 上的通道都必须集成 SelectableChannel.

* ServerSocketChannel: 服务器端通道. 只有通过这个通道应用程序才能向操作系统注册支持 "多路复用IO" 的端口监听. 同时支持
  UDP 协议和 TCP 协议. 可关注事件: SelectionKey.OP_ACCEPT.
* SocketChannel: TCP协议通道, 一个 Socket 套接字对应了一个 客户端IP:端口 到 服务器IP:端口 的通信连接. 可关注事件:
  SelectionKey.OP_READ, SelectionKey.OP_WRITE, SelectionKey.OP_CONNECT.
* DatagramChannel: UDP协议通道. 可关注事件: SelectionKey.OP_READ, SelectionKey.OP_WRITE.
* FileChannel: 面向文件的(可以理解为内存就是文件), 只要两端有一个是 FileChannel 就可以直接操作 Buffer.

##### Buffer(数据缓存区)

在 JAVA NIO 框架中, 为了保证每个通道的数据读写速度 JAVA NIO 框架为每一种需要支持数据读写的通道集成了 Buffer 的支持.

**注: ServerSocketChannel 通道它只支持对 OP_ACCEPT 事件的监听, 所以它是不能直接进行网络数据内容的读写的. 所以
ServerSocketChannel 是没有集成 Buffer 的.**

**Buffer 有两种工作模式: 写模式和读模式.**

在读模式下, 应用程序只能从Buffer中读取数据, 不能进行写操作.
但是在写模式下, 应用程序是可以进行读操作的, 这就表示可能会出现脏读的情况.
所以一旦决定要从 Buffer 中读取数据, 一定要将 Buffer 的状态改为读模式.

综上, Buffer 本身就是一块内存, 底层是线上它实际上是个数组, 数据的读写操作都是通过 Buffer 来实现的.
其本身是线程不安全的, 需要开发者自己去处理并发问题

`0 <= mark <= position <= limit <= capacity`

* capacity: 缓存区的最大容量, 其不可能为负数, 也永远不会变化. 这个容量是在缓存区创建时进行指定的. 由于高并发时通道数量往往会很庞大,
  所以每一个缓存区的容量最好不要过大.
* limit: 是不应该读或写的第一个元素的下标(从 0 开始), 如一个 Buffer 的 capacity 为 6, 则 6 个元素的下标为 0-5,
  而其第一个不能读也不能写的下标为 6, 但其实并没有 6 这个下标, 小于等于 capacity.
* position: Buffer 下一个将要去读或写的下标位置, 如一个 capacity 为 6 的 Buffer, 写入了 3 个元素, 那么此时其 position
  的值应该是 3(下一个读写的元素是第四个元素, 下标为 3), 大于等于 0, 小于 limit.

###### allocate(int capacity)

    分配一个内部数组长度为 capacity 的 Buffer

###### wrap(char[] array)

    直接将 array 包装成一个 Buffer, Buffer 的 capatity = array.length()

###### flip()

    反转这个 Buffer 的读写状态, 如果是读会改为写, 如果是写则改为读,
    读 -> 写, position 会设置为当前内容的最后一位, limit 则设置为等于 capacity
    写 -> 读, position 设置为 0, limit 设置为当前内容的最后一位
    但是内部数组中的内容是不会被清空的

    具体操作如下:
        现有一个 capaticy = 6 的 IntBuffer, 向里面放入了 1,2,3,4 四个数字进去
        那么其属性应为: capacity = 6, limit = 6, position = 4
        然后执行完 flip() 方法之后其变为只读状态, 具体过程如下:
            1: limit = position, 将 position 赋值给 limit, 此时 limit = 4
            2: position = 0, position 归零, 此时 position 的结果与初始结果一致
        此时就可以对 [position, limit) 范围来进行顺序读取

##### mark()

    做一个标记, 即 mark = position, 当调用 reset() 方法后, 读的位置会回到 mark() 标记到的位置, mark 大于等于 0 且小于 position

##### reset()

    重新将 position 定位到 mark() 设置的位置

###### clear()

    将 Buffer 恢复到初始化的状态, 设置 position = 0, limit = capacity, 此时为写模式

###### rewind()

    让一个 Buffer 准备重新再读取一次, 其它属性不变, position=0

###### isReadOnly()

    并不是所有的 Buffer 都是可以进行写操作的 , 有的 Buffer 只允许进行读操作 , 称之为 只读 Buffer , 
    只读Buffer 只是针对其元素来说的 , 其中 limit 和 position 等属性还是可以被修改的
    通过 asReadOnlyBuffer() 方法可以得到一个只读 Buffer 
    一个普通 Buffer 可以随时得到一个只读 Buffer , 但是一个只读 Buffer 是不可能得到一个普通 Buffer 的

###### slice()

    根据设定好的 position 和 limit 得到一个新的 Buffer, 称之为切面 Buffer, 切面 Buffer 的值和原 Buffer 的值共用一个数组

##### Selector(选择器)

    事件订阅和 Channel 管理: 应用程序将向 Selector 对象注册需要它关注的 Channel 以及具体的某一个 Channel 会对哪些 IO 事件感兴趣. Selector 中也会维护一个 "已经注册的Channel" 的容器.
    轮询代理: 应用层不再通过阻塞模式或者非阻塞模式直接询问操作系统 "事件有没有发生", 而是由 Selector 代其询问.
    实现不同操作系统的支持: 多路复用 IO 技术是需要操作系统进行支持的, 其特点就是操作系统可以同时扫描同一个端口上不同网络连接的时间. 所以作为上层的 JVM 必须要为不同操作系统的多路复用 IO 实现编写不同的代码.

#### AIO(Asynchronous IO)(异步非阻塞)

    应用程序在发起 IO 调用之后, 用户进程立即返回, 内核等待数据准备完成, 然后将数据拷贝到用户进程缓冲区, 最后发送信号告诉用户进程 IO 操作执行完毕, 此次操作为异步 IO.
    异步 IO 真正实现了 IO 全流程的非阻塞, 用户线程完全不需要关心实际的整个 IO 操作是如何进行的, 只需要先发起一个请求, 当接收到内核返回的成功信号时表示 IO 操作已经完成, 可以直接去使用数据了.