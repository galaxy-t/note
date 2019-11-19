package com.galaxyt.note.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class NIOServer {


    // 通道管理器
    private static Selector selector;
    private static LinkedBlockingQueue<SelectionKey> requestQueue;
    private static ExecutorService threadPool;

    public static void main(String[] args) {
        init();
        listen();
    }

    private static void init() {

        // 通道
        ServerSocketChannel serverSocketChannel = null;

        try {
            // 初始化通道管理器
            selector = Selector.open();

            // 初始化一个服务端监听通道
            serverSocketChannel = ServerSocketChannel.open();
            // 将Channel设置为非阻塞的，NIO就是支持非阻塞的
            serverSocketChannel.configureBlocking(false);
            // 设置该通道要监听的端口为 9000，并设置请求接入的最大长度为 100
            serverSocketChannel.socket().bind(new InetSocketAddress(9000), 100);
            // 将该通道交给通道管理器管理，并为该通道注册SelectionKey.OP_ACCEPT事件
            // 注册该事件后，当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
            //就是仅仅关注这个ServerSockerChanner接收到的TCP连接的请求
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化一个长度为 500 的阻塞队列
        requestQueue = new LinkedBlockingQueue<SelectionKey>(500);

        // 初始化一个大小为 10 的线程池
        threadPool = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            threadPool.submit(new Worker());
        }


    }

    private static void listen() {
        while (true) {
            try {
                // 循环监听接收消息
                selector.select();
                // 如果接收到请求会进行到这一步，提取本次接收到的这一组消息
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) { //迭代这一组消息

                    SelectionKey key = keyIterator.next();
                    // 可以认为一个SelectionKey是代表了一个请求
                    // 因为上面的 key 这个变量已经得到了本次迭代的引用，现在可以将这个引用从迭代器中移除，以防止重复消费发生
                    keyIterator.remove();
                    // 处理这个请求
                    handleRequest(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleRequest(SelectionKey key) throws IOException {

        // 定义一个也客户端进行连接的通道
        SocketChannel channel = null;

        try {
            // 如果说这个Key是一个acceptable,也就是一个连接请求
            if (key.isAcceptable()) {

                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                // 调用 accept 这个方法，就可以进行 TCP 三次握手了
                channel = serverSocketChannel.accept();
                // 握手成功的话就可以获取到一个 TCP 连接好的 SocketChannel
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                //仅仅关注这个READ请求，就是客户端发送数据过来的请求
            } else if (key.isReadable()) { // 如果说这个key是readable，是个发送了数据过来的话，此时需要读取客户端发送过来的数据

                channel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int count = channel.read(buffer);

                // 通过底层的socket读取数据，写buffer中，position可能就会变成21之类的

                // 你读取到了多少个字节，此时buffer的position就会变成多少

                if (count > 0) {

                    // 准备读取刚写入的数据，就是将limit设置为当前position，将position设置为0，丢弃mark。一般就是先写入数据，接着准备从0开始读这段数据，就可以用flip

                    // position = 0，limit = 21，仅仅读取buffer中，0~21这段刚刚写入进去的数据

                    buffer.flip();

                    System.out.println("服务端接收请求" + new String(buffer.array(), 0, count));
                    channel.register(selector, SelectionKey.OP_WRITE);
                }
            } else if (key.isWritable()) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put("收到".getBytes());
                buffer.flip();

                channel = (SocketChannel) key.channel();
                channel.write(buffer);
                channel.register(selector, SelectionKey.OP_READ);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();

            if (channel != null) {
                channel.close();
            }
        }
    }


    //创建一个线程任务来执行
    static class Worker implements Runnable {


        public void run() {

            while (true) {
                try {
                    SelectionKey key = requestQueue.take();
                    handleRequest(key);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }



    }



}
