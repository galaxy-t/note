# CentOS7 安装 jdk


### 官网下载对应版本的 JDK 如 : jdk-8u251-linux-x64.tar.gz

### 解压 tar -zxvf jdk-8u251-linux-x64.tar.gz

### 将解压后的文件移动到 /usr/local 并重命名 mv jdk1.8.0_251/ /usr/local/jdk8

### 配置环境变量

    编辑 /etc/profile 文件  vim /etc/profile
    在文件的最下方加入下方三行 , 注意 JAVA_HOME 的路径

    export JAVA_HOME=/usr/local/jdk8
    export CLASSPATH=.:${JAVA_HOME}/jre/lib/rt.jar:${JAVA_HOME}/lib/dt.jar:${JAVA_HOME}/lib/tools.jar
    export PATH=$PATH:${JAVA_HOME}/bin
    
### 使刚配置的环境变量生效

    source /etc/profile
    
### 测试是否安装成功

    java -version
