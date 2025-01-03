# IDEA

***

### 新装机基础设置
    
    在重装系统之后 , 或者系统刚装 IDEA 的时候 , 依照本人习惯需要做的一些设置或需要安装的一些插件
    
1. 全局 MAVEN 设置 ( PS : 先设置这个 , 不然一下载项目 IDEA 会在一个默认的路径下载 jar 包 , 很麻烦 , 在此之前要先有 MAVEN , MAVEN 也要先把仓库的路径设置好 )

    > File --- Other Settings --- Settings for new projects --- Build Tools ---  Maven
    
    > 然后直接设置 MAVEN 配置文件的路径即可

2. 快捷键设置 , Mac 上不需要额外的操作 , Windows 上使用 Default for Windows 需要设置关闭当前文件和删除整行的快捷键
    
    > 设置路径 File --- Setting --- Keymap

    > Ctrl + W , 右侧搜索 close , 在 Window --- Editor Tabs --- Close 下直接更改其快捷键

    > Ctrl + D , 右侧搜索 delete , 在 Editor Actions --- Delete Line 下直接更改其快捷键                                                                                                                            

3. 设置代码不自动收缩 ( PS : 默认情况下 , 会把类似于实体类的 getter/setter 这种短方法收缩到一行去 )

    > File --- Settings --- Editor --- General --- Code Folding , 取消勾选 One-line methods

4. 设置鼠标移动显示注释

    > File --- Settings --- Editor --- General 右侧 Other , 勾选 Show quick documentation on mouse move
    
5. VIM 模式

    > 点击 Tools --- Vim Emulator 进行启用和禁用
                
6. 自定义类注释 , 其它注释还好说 , 类注释默认的不好 , 一般习惯使用模板注释

    > File --- Settings --- Editor --- Live Templates , 右侧加号 , 可以先添加一个分组 , 也可以直接添加模板 , Abbreviation : 输入什么出来这个模板 , 一般用 class , Description : 描述该快捷模板

```$xslt
    /**
     *
     * @author zhouqi
     * @date $DATE$ $TIME$
     * @version v1.0.0
     * @Description
     *
     * Modification History:
     * Date                 Author          Version          Description
    ---------------------------------------------------------------------------------*
     * $DATE$ $TIME$     zhouqi          v1.0.0           Created
     *
     */
   
```

    > 以上模板中存在两个变量 , 需要给他们提供对应的方法 , 点击 Edit variables , $DATE$=date() , $TIME$=time()

7. 添加 git push 图标 ( PS : IDEA 默认不显示 push 图标 )

    > File --- Settings --- Appearance & Behavior --- Menus and ToolBars --- Navigation Bar Toolbar --- NavBarVcsGroup --- VscNavBarToolBarActios --- VCS Label
    > 选中点击上方加号 , Add Action , 在弹出框搜索 git , 然后在目录下找到 push , 点击 ok 即可 , 可以选中 , 点击上方的上下按钮调节位置顺序

8. 隐藏不需要的文件

    > File --- Settings --- Editor --- File Types , 在最下面的输入框中添加 .idea;*.iml;target; , 注意分号分隔

***

### IDEA 常用插件
1. Lombok
    
    > Lombok 简洁开发所需要使用到的插件
    
    > File --- Settings --- Plugins , 搜索 lombok , 第一个 Lombok plugin
    
2. Docker Integration

    > IDEA 自带的一个插件貌似(具体忘了是自带的还是安装上的了，去插件库里搜一下不就知道了)
    
    > 首先使用必须先在项目里，我喜欢在根目录下写一个 Dockerfile，格式如下
    
      `
        # 指定基础镜像，必须为第一个命令
        # 格式
        #   FROM <image>
        #   FROM <image>:<tag>
        #   FROM <image>@<digest>
        # tag或digest是可选的，如果不使用这两个值时，会使用latest版本的基础镜像
        FROM java:8
        # MAINTAINER: 维护者信息
        MAINTAINER galaxy-t
        # 指定于外界交互的端口
        EXPOSE 8083
        # 用于指定持久化目录
        VOLUME /tmp
        # 用于指定传递给构建运行时的变量，此处的 JAR_FILE 被配置在 pom 文件中
        # ARG JAR_FILE
        # 将本地文件添加到容器中，tar类型文件会自动解压(网络压缩资源不会被解压)，可以访问网络资源，类似wget
        ADD /target/user-1.0-SNAPSHOT.jar app.jar
        # 配置容器，使其可执行化
        ENTRYPOINT ["java","-jar","/app.jar"]
      `
    > 有了 Dockerfile 打开它，你会发现在它 FROM 那一行左边有一个不对是两个（重叠着的）向右的三角（也可能叫做箭头吧，这也不能叫箭头），
    > 点它，有三个选项，最后一个，New***（第二次会变成 Edit*** ） 点击，基本上是不用动太多，注意改动几个地方即可，其它的默认就行了
    > Image tag : 这个玩意主要是为了你把项目打包成镜像之后镜像叫什么名字，最好改一下，否则 Docker 会自动生成一个，不好识别
    > Container name : 这个是你在打包上传完镜像然后启动的那个容器的名字，最好改一下，否则 Docker 会自动生成一个，不好识别
    > 以上两个是为了好识别，如果你不愿意改动那也不会发生什么本质上的问题，顶多费眼睛，下面的必须得注意，划重点了啊
    > Run options : -d -v /home/app/user:/tmp --privileged=true --net=host
    > 这个指令就不过多解释了，就是首次启动容器的时候指定的那些，可以根据实际情况自己改一下
    > 安装配置都完事了，怎么用呢，注意看 Dockerfile 中的 ADD 那一行， /target/*****，插件会到这个目录下找你的 jar 包打镜像
    > 所以，你需要先  mvn clean compile package 一下，当然了，也可以直接使用 IDEA 的 maven 插件
    
    
                  

2. RestfulToolki
    
    > 一个类似于 Postman 的小工具，凑活用挺方便的，还能自动生成参数 json

3. VisualVM Launcher

    > Mac 上配置 : Preferences -> Other Settings -> VisualVM Launcher 
    > VisualVM executable : /Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/bin/jvisualvm
    > JDK home : /Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home
    > 注意不同的 JDK 版本路径存在不同
    
***
    
### 快捷键
    
    Ctrl + 鼠标左键 : 直接打开该方法或引用
    Ctrl + Alt + 鼠标左键 : 打开该方法的实现 , 用于查看接口的实现方法
    
    option	+ F12				打开终端
    option 	+ command + L		格式化代码
    option	+ enter				自动导包
    control + option  + O       自动删除多余的包
    command + F9				手动编译项目
    shift * 2					快速访问，检索全部包括工具
    command + shift + F12		关闭所有工具框，最大化编辑框
    command + O 				检索Class，类名检索
    command + shift + O 		检索Files，FileName检索
    command + option + O 		检索方法名
    command + E 				最近访问的文件
    command + F12				文件结构
    option  + shift + 上下箭头	上下移动当前选定行
    command + delete			删除行
    option  + enter				显示意图行动，类似于Eclipse的control + 1
    control + H					选中的文件结构
    command + shift + H			选中的方法的结构
    control + shit  + delete	最后修改位置
    control + R					运行
    control + D 				调试
    command + option + T		进行代码环绕，如判断、循环、try等
    command + option + J		使用实时模版环绕
    command + B					导航到变量声明位置
    command + shift  + F 		全文检索
    command + D 				复制一行
    command + N  				自动生成构造函数，get、set等
    command + shift + enter		完成代码结构
    control + space				基本完成，类似于代码提示
    tontrol + J					查看方法
    control + Z                 撤销
    control + shift + Z         反向撤销
    alt + 数字                   各种栏位切换
    alt + 左右箭头                切换 tab 页
***
    
### 一些其它便捷操作

1. 创建 XML 文件

    > idea 创建 xml 的时候 , 右键 new 找不到 xml 的选项 , 应该选择 Resource Bundle , 输入文件名 ,选中 Use XML-based properties files
    
2. psvm , 快速生成main函数
    
3. sout , 打印输出
    
4. psf , 定义静态常量

5. 调试
        
        Eclipse 				IDEA
        ================================
        F5						F7
        						shift + F7
        F6						F8
        F7						shift + F8
        F8						command + option + R
        Ctrl+Shift+B			command + F8				切换断点
        						option  + F8				评估表达