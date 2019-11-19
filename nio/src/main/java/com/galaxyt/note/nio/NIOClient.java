package com.galaxyt.note.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOClient {

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            new Worker().start();
        }

    }

    static class Worker extends Thread {
        @Override
        public void run() {
            SocketChannel channel = null;
            Selector selector = null;

            try {
                // SocketChannel，一看底层就是封装了一个Socket
                channel = SocketChannel.open(); // SocketChannel是连接到底层的Socket网络
                // 数据通道就是负责基于网络读写数据的
                channel.configureBlocking(false);
                channel.connect(new InetSocketAddress("localhost", 9000));
                // 后台一定是tcp三次握手建立网络连接
                selector = Selector.open();
                // 监听Connect这个行为
                channel.register(selector, SelectionKey.OP_ACCEPT);

                while (true) {
                    // selector多路复用机制的实现  循环去遍历各个注册的Channel
                    selector.select();
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        // 拿到当前行为
                        SelectionKey key = keyIterator.next();
                        // 删除已选的key,以防重复处理
                        keyIterator.remove();

                        // 如果发现返回的时候一个可连接的消息 走到下面去接受数据
                        if (key.isConnectable()) {
                            channel = (SocketChannel)key.channel();

                            if (channel.isConnectionPending()) {
                                channel.finishConnect();

                                // 接下来对这个SocketChannel感兴趣的就是人家server给你发送过来的数据了
                                // READ事件，就是可以读数据的事件
                                // 一旦建立连接成功了以后，此时就可以给server发送一个请求了

                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                buffer.put("你好".getBytes());
                                buffer.flip();
                                channel.write(buffer);
                            }
                            channel.register(selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {  //这里的话就时候名服务器端返回了一条数据可以读了

                            channel = (SocketChannel) key.channel();
                            // 构建一个缓冲区
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            // 把数据写入buffer，position推进到读取的字节数数字
                            int len = channel.read(buffer);
                            if (len > 0) {
                                System.out.println("[" + Thread.currentThread().getName() + "]收到响应：" + new String(buffer.array(), 0, len));
                                Thread.sleep(5000);
                                channel.register(selector, SelectionKey.OP_WRITE);
                            }
                        } else if (key.isWritable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.put("你好".getBytes());
                            buffer.flip();
                            channel = (SocketChannel) key.channel();
                            channel.write(buffer);
                            channel.register(selector, SelectionKey.OP_READ);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (selector != null) {
                    try {
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
    }

}
