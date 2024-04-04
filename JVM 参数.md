# JVM 参数

    jvm在启动的时候，会执行默认的一些参数。
    一般情况下，这些设置的默认参数应对一些平8度。（PS：一步一步来我也记不住，偷偷告诉你们，我到现在都无法凭记忆把 JDK 的环境变量配好，丢人了。）
    
    
4. -XX:+PrintGCTimeStamps

    打印GC发生的时间戳。格式如下：
    
    289.556: [GC [PSYoungGen: 314113K->15937K(300928K)] 405513K->107901K(407680K), 0.0178568 secs] [Times: user=0.06 sys=0.00, real=0.01 secs] 

    293.271: [GC [PSYoungGen: 300865K->6577K(310720K)] 392829K->108873K(417472K), 0.0176464 secs] [Times: user=0.06 sys=0.00, real=0.01 secs]
    
    解读：289.556表示从jvm启动到发生垃圾回收所经历的的时间。GC表示这是新生代GC（Minor GC）。PSYoungGen表示新生代使用的是多线程垃圾回收器Parallel Scavenge。->15937K(300314113K928K)]这个跟上面那个GC格式一样。
    
    只不过，这个是表示的是新生代，幸存者区。后面那个是整个堆的大小，GC前和GC后的情况。Times这个显而易见，代表GC的所消耗的时间，用户垃圾回收的时间和系统消耗的时间和最终真实的消耗时间。
    
5. -X:loggc:log/gc.log

    这个就表示，指定输出gc.log的文件位置。（我这里写的log/gc.log就表示在当前log的目录里，把GC日志写到叫gc.log的文件里。）
    
6. -XX:+PrintHeapAtGC

    表示每次GC后，都打印堆的信息。（这个打印的基本格式跟上面第二条的基本类似，我也就不比多说了。）
    
7. -XX:+TraceClassLoading

    监控类的加载。格式如下：
    
    •[Loaded java.lang.Object from shared objects file]
    •[Loaded java.io.Serializable from shared objects file]
    •[Loaded java.lang.Comparable from shared objects file]
    •[Loaded java.lang.CharSequence from shared objects file]
    •[Loaded java.lang.String from shared objects file]
    •[Loaded java.lang.reflect.GenericDeclaration from shared objects file]
    •[Loaded java.lang.reflect.Type from shared objects file]
    
    使用这个参数就能很清楚的看到那些类被加载的情况了。
    
8. -XX:+PrintClassHistogram

    跟踪参数。这个按下Ctrl+Break后，就会打印一下信息：
    
    num     #instances         #bytes  class name
    
    ----------------------------------------------
    
       1:        890617      470266000  [B
    
       2:        890643       21375432  java.util.HashMap$Node
    
       3:        890608       14249728  java.lang.Long
    
       4:            13        8389712  [Ljava.util.HashMap$Node;
    
       5:          2062         371680  [C
    
       6:           463          41904  java.lang.Class
       
    分别显示：序号、实例数量、总大小、类型。
       
    这里面那个类型，B和C的其实就是byte和char类型。
