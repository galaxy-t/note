# 类装载器(ClassLoader)

    负责加载 class 文件, class 文件在文件开头有特定的文件标识, 并且 ClassLoader 只负责 class 文件的加载,
    至于它是否可以运行, 则由执行引擎(Execution Engine) 决定.

## 装载过程

    一个静态 class 文件, 如 Car.class 文件, 经过 ClassLoader 之后(加载并初始化), 会变成一个 Class<Car> 这样一个对象(类似于模板的概念),
    然后 Class<Car> 可以被实例化成 car1, car2, car3 等多个具体的对象实例

![005.png](img%2F005.png)

1. Car.class
2. ClassLoader
3. Class<Car>
4. new Car()
5. car1,car2,car3

##### 在 java 代码中使用 Car.getClass().getClassLoader(); 可以获得使用的类加载器

## 类型

![006.png](img%2F006.png)

1. 虚拟机自带的加载器
2. 启动类加载器(Bootstrap Class Loader)
3. 扩展类加载器(Extention Class Loader)
4. 应用程序类加载器(App Class Loader)/系统类加载器(System Class Loader)
5. 自定义类加载器

类加载器是分上下级关系的, 最上层是 BootstrapClassLoader, 其次是 ExtentionClassLoader, 再然后是 SystemClassLoader

### 启动类加载器(Bootstrap Class Loader)

使用 C++ 编写

用于加载 $JAVA_HOME/jre/lib/rt.jar 库中的类

验证: new Test4().getClass().getClassLoader().getParent().getParent(), 会返回 null,
因为是 C++ 写的, 所以拿不到, 返回 null

### 扩展类加载器(Extention Class Loader)

使用 Java 编写

用于加载 $JAVA_HOME/jre/lib/*.jar 这些库中的类

验证: new Test4().getClass().getClassLoader().getParent(), 会打印 ExtClassLoader

### 应用程序类加载器(App Class Loader)/系统类加载器(System Class Loader)

使用 Java 编写

用于加载当前应用的 classpath 的所有类(CLASSPATH)

验证: new Test4().getClass().getClassLoader(), 会打印 AppClassLoader, 因为 Test4 类是自己开发的, 所以使用了应用程序类加载器.

### 用户自定义类加载器

java.lang.ClassLoader 的子类, 用户可以定制类的加载方式

### 注: new Object().getClass().getClassLoader() 会返回空, 因为 Object 类的类加载器是 BootstrapClassLoader, Object 类存在于 rt.jar 中

## 沙箱机制

沙箱机制是一种安全机制, 用来保护 JVM或JAVA 的运行环境不被破坏的一种机制.
是一个目标, 意思是需要达到这种效果.

默认情况下, 针对一些类或者操作都需要有一些权限, jvm 默认设置了很多,
如在反射的时候默认 private 属性无法被直接访问, 代码如下

```
User user = new User();                                     // 创建一个对象
Field name = user.getClass().getDeclaredField("name");      // 拿到 name 这个属性的反射对象
name.setAccessible(true);                                   // 修改访问权限为 true
name.set(user, "aaa");     
```

以上代码中 .setAccessible(true) 的操作就是打破默认的沙箱安全机制的一种做法

## 双亲委派

双亲委派是沙箱机制的一种实现

### 概念

当一个自定义类被加载的时候会被首先交给到 SystemClassLoader, 但是 SystemClassLoader 不会立刻加载这个自定义类, 它会先委派
他的上级也就是 ExtentionClassLoader 去加载, ExtentionClassLoader 也会这样做, 一直委派到 BootstrapClassLoader,
BootstrapClassLoader 会检查该类是否应该被自己加载, 如果是就加载, 不是则会返回调用者, 也就是他的下级, 最后再返回到
SystemClassLoader, 如果上级都没有被加载就才会被 SystemClassLoader 来加载.

在我们自己开发的一个自定义类被加载的时候, 其加载过程如下

1. 首先给到 SystemClassLoader
2. SystemClassLoader 会委派它的上级去加载, 即 SystemClassLoader 又会将这个类给到 ExtentionClassLoader
3. ExtentionClassLoader 也会委派它的上级去加载, 即 ExtentionClassLoader 也会将这个类给到 BootstrapClassLoader
4. BootstrapClassLoader 尝试去加载这个类, 发现这个类不应该被自己加载, 然后返回到 ExtentionClassLoader
5. ExtentionClassLoader 也发现不应该由自己加载这个类, 然后返回到 SystemClassLoader
6. SystemClassLoader 发现该类应该由自己加载, SystemClassLoader 加载该类

从 ClassLoader 类的 loadClass() 方法的源码就能很明显能够看出双亲委派机制

### 如何打破双亲委派机制

代码如下

```java

public class Test3 {


    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        ClassLoader myClassLoader = new ClassLoader() {
            // name 传入格式: com.galaxyt.Test3.Class
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {

                // 将 name 处理成文件名: com.galaxyt.Test3.class
                String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                InputStream in = getClass().getResourceAsStream(fileName);
                if (in == null) {
                    return super.loadClass(name);
                }

                try {
                    byte[] buff = new byte[in.available()];
                    in.read(buff);

                    return defineClass(name, buff, 0, buff.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException();
                }
            }
        };

        System.out.println(myClassLoader.getClass().getClassLoader());                  // sun.misc.Launcher$AppClassLoader@, 由系统加载器加载
        System.out.println(Test3.class.getClassLoader());                               // sun.misc.Launcher$AppClassLoader@, 由系统加载器加载
        Object obj = myClassLoader.loadClass("com.galaxyt.Test3").newInstance();        // 

        System.out.println(obj.getClass().getClassLoader());                            // com.galaxyt.Test3$1@, 由自定义类加载器加载
        System.out.println(obj instanceof Test3);                                       // false, 不同的 ClassLoader 加载出来的相同的类是不同的, 即在同一个 jvm, 同一个类被加载了两次, 产生了不同的 Class<Test3> 对象 
    }
}

```

以上代码中以匿名类的形式自己定义了一个 ClassLoader, 从结果可以看出, 相同的类被不同的 ClassLoader 加载的时候, 他们是不一样的,
是两种对象(两种类).
即所谓的名称空间的概念.

1. 只有被同一个类加载器加载的类才可能会相等
2. 不同的类加载器加载的类, 即便他们本身是相同的, 也不相等
