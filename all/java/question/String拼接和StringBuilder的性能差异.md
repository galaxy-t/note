# String拼接和StringBuilder 的性能差异

## 结论

    使用 StringBuilder 性能更好

## 分析

使用 javap -verbose 命令将一段测试代码的字节码文件反编译之后可以看出,
每一个 String 对象的底层都是一个 StringBuilder 对象.

如果使用 String 拼接的方式, 那么每拼接依次都会创建一个 StringBuilder 对象,
然后执行 append 函数, 然后再执行 toString 函数.

实际 String 的构建过程如下:

1. 先创建一个 StringBuilder
2. 执行 append 函数
3. 执行 toString 函数, 得到一个字符串
4. 每次拼接都是这么个操作
