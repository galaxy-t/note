# 类文件(class 文件)

[官方文档](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1)

## 设计理念

##### 所有按照 class 文件标准的语言都可以在 jvm 执行, 如: java, groovy, jruby, scala

## 文件结构

class 文件是一组以 8 位字节位基础单位的二进制流,
各个数据项目严格按照顺序紧凑的排列在 class 文件之中, 中间没有添加任何分隔符,
这使得整个 class 文件中存储的内容几乎全部是程序运行的必要数据, 没有空隙存在.

当遇到需要占用 8 位字节以上空间的数据项时, 则会按照高位在前(Big-Endian)的方式分隔成若干个 8 位字节进行存储.

class 文件只有两种数据类型: 无符号数(基础类型)和表(引用类型)

## 二进制的优势

1. 节省空间
2. 效率高, 不需要额外的编解码操作

## 魔术

当用记事本打开一个 class 文件后能够发现, 它最开始的前 64 位永远都是 'cafa babe',
这是 16 禁止的表达方式, 每一个字母占 8 位, 一共 64 位.

## class 文件内容

使用命令 javac -g:vars 可以将 java 文件编译成 class 文件

使用命令 javap -verbose 可以将 class 文件反编译成指令集

## 指令集

有代码如下

```java
package com.galaxyt;

public class Test3 {

    public static void main(String[] args) {

        int a = 2;
        int b = 400;
        int c = a + b;
        System.out.println(c);
    }

}
```

其 class 反编译后的指令集如下

```text
Classfile /C:/dev/workspace/galaxyt/test1/src/main/java/com/galaxyt/Test3.class
  Last modified 2024-3-10; size 483 bytes
  MD5 checksum 3ca1110bc6f20733aaab37951dec31f4
public class com.galaxyt.Test3
  minor version: 0
  major version: 61
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
   #2 = Class              #4             // java/lang/Object
   #3 = NameAndType        #5:#6          // "<init>":()V
   #4 = Utf8               java/lang/Object
   #5 = Utf8               <init>
   #6 = Utf8               ()V
   #7 = Fieldref           #8.#9          // java/lang/System.out:Ljava/io/PrintStream;
   #8 = Class              #10            // java/lang/System
   #9 = NameAndType        #11:#12        // out:Ljava/io/PrintStream;
  #10 = Utf8               java/lang/System
  #11 = Utf8               out
  #12 = Utf8               Ljava/io/PrintStream;
  #13 = Methodref          #14.#15        // java/io/PrintStream.println:(I)V
  #14 = Class              #16            // java/io/PrintStream
  #15 = NameAndType        #17:#18        // println:(I)V
  #16 = Utf8               java/io/PrintStream
  #17 = Utf8               println
  #18 = Utf8               (I)V
  #19 = Class              #20            // com/galaxyt/Test3
  #20 = Utf8               com/galaxyt/Test3
  #21 = Utf8               Code
  #22 = Utf8               LocalVariableTable
  #23 = Utf8               this
  #24 = Utf8               Lcom/galaxyt/Test3;
  #25 = Utf8               main
  #26 = Utf8               ([Ljava/lang/String;)V
  #27 = Utf8               args
  #28 = Utf8               [Ljava/lang/String;
  #29 = Utf8               a
  #30 = Utf8               I
  #31 = Utf8               b
  #32 = Utf8               c
{
  public com.galaxyt.Test3();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/galaxyt/Test3;

  public static void main(java.lang.String[]);                                                                          # 函数名
    descriptor: ([Ljava/lang/String;)V                                                                                  # (参数列表)返回值, 前面带 L 表示是引用类型
    flags: ACC_PUBLIC, ACC_STATIC                                                                                       # 访问关键字, 公共函数和静态函数标记
    Code:                                                                                                               # 指令集代码
      # stack=2: 操作数栈的深度为 2
      # locals=4: 本地变量表的槽位数量(单位: slot(槽的意思)) 为 4, 64位的变量(如: lang 类型) 占 2 个槽位, 其它占 1 个槽位, 索引从 0 开始, 如果是非 static 方法, 索引 0 代表 this
      # args_size=1: 表示有一个参数, 实例方法多一个 this 参数 
      stack=2, locals=4, args_size=1                                                                                    # 
         0: iconst_2                                                                                                    # 常量 2 压栈
         1: istore_1                                                                                                    # 出栈保存到本地变量 1 里面(本地变量表中的第一个)
         2: sipush        400                                                                                           # 400 压栈
         5: istore_2                                                                                                    # 出栈保存到变量 2 里面
         6: iload_1                                                                                                     # 变量 1 压栈
         7: iload_2                                                                                                     # 变量 2 压栈
         8: iadd                                                                                                        # 调用 CPU, 进行栈顶两个元素相加, 计算结果压栈
         9: istore_3                                                                                                    # 出栈保存到变量 3 里面
        10: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;                       # 调用 static 指令, 相当于把 System.out 的引用(Ljava/io/PrintStream;)加载进来
        13: iload_3                                                                                                     # 变量 3 压栈
        14: invokevirtual #13                 // Method java/io/PrintStream.println:(I)V                                # 调用一个实例方法打印, 会打印栈顶内容
        17: return                                                                                                      # 返回
      LineNumberTable:                          # java 代码行号与本地变量表的对应关系, jdk 17 不是这个样子的, 这段代码是额外复制进来的
        line 7: 0                               # 第 7 行 java 代码定义的变量对应本地变量表的开始位置是 0, 0 不是槽位的下标, 是开始位置
        line 8: 2                               # 第 8 行 java 代码定义的变量对应本地变量表的开始位置是 2, 2 不是槽位的下标, 是开始位置
        line 7: 6
        line 11: 10
      LocalVariableTable:                                                                                               # 本地变量表
        # Start: 从哪里开始
        # Length: 长度
        # Slot: 槽位下标
        # Name: 变量名称
        # Signature: 类型
        Start  Length  Slot  Name   Signature
            0      18     0  args   [Ljava/lang/String;
            2      16     1     a   I
            6      12     2     b   I
           10       8     3     c   I
}
```

上述中的字节码指令解释如下

1. iconst_2: 常量 2 压栈, 将 2 这个常量放入栈中
2. istore_1: 做一次出栈操作, 将结果保存给变量 1
3. iload_1:  将变量 1 进行压栈
4. iadd:     栈顶做两次出栈操作, 出来的值放入寄存器中, CPU 会将寄存器中存储的值相加后将结果入栈
5. sipush:   执行一次压栈操作, 压栈内容直接写死
6. return:   返回操作, 即使没有写 return, 字节码指令中也会进行 return

iconst_2 指令分段解释

1. iconst: 操作码
    1. i: int 类型
    2. const: 常量
2. 2: 操作数, 指数字 2
