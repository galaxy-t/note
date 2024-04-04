# finally 中修改返回值内容, 返回的结果是否会发生改变

## 问题

有示例代码如下:

```
public static String f2() {
    String str = "hello";
    
    try {
        return str;
    } finally {
        str = "world";
    }
}
```

以上代码的返回值是 hello 还是 world

## 结论

会返回 hello

## 分析

使用 javap 可以得到指令集如下:

```
 public static java.lang.String f2();
    descriptor: ()Ljava/lang/String;            // 没有参数, 返回值为
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=1, locals=3, args_size=0
         0: ldc           #19                 // String hello       // 从常量区拿到 19 号位的常量, 内容为 hello
         2: astore_0                                                // a 代表 ASCII 也就是字符串的意思, astore_0 的意思是存储到第 0 个变量且变量类型为 string
         3: aload_0                                                 // 将刚才的结果存储给第 0 个变量并且压栈
         4: astore_1                                                // 做一次出栈操作, 将结果给到第 1 个变量(这实际上是 return 的一个变量)        ## 实际上这四行就是在完成 String str = "hello"; 这一行代码的操作
         5: ldc           #21                 // String world       // 从常量区拿到 21 号位的常量, 内容为 world
         7: astore_0                                                // 将 world 的结果存储给第 0 个变量
         8: aload_1                                                 // 拿到变量 1 的值
         9: areturn                                                 // 返回, 实际上在这里返回值就已经定了, 第 1 个变量为返回值变量, 在这之前返回值变量只被修改过一次, 此时返回的就是这个值, 后面也没有修改过这个值 
        10: astore_2
        11: ldc           #21                 // String world
        13: astore_0
        14: aload_2
        15: athrow
```

分析上面的指令集可以看到, return 是分成两个步骤的

1. 将 return 的结果放到一个变量中, 该步骤是会在 return 这行代码被调用的时候执行
2. 在方法的最后执行 return 返回存储 return 结果的变量, 这个是在整个方法执行完毕被执行
