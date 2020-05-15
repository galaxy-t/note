### Maven 


### 注

    在使用阿里云代理的时候,配置如下
    
        <mirror>
          <id>aliyunmaven</id>
          <mirrorOf>*</mirrorOf>
          <name>阿里云公共仓库</name>
          <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
        
    然后在 IDEA 中使用 MAVEN 的可视化工具,也就是右侧点击 clean 等命令会出错
    原因是 url 中是使用的 https 
    可本地也没有证书所以导致下载失败 
    
    两种方式
    1: 直接使用  mvn clean compile package -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
        这样会跳过 ssl 验证
        
    2: 在 IDEA 的 MAVEN 设置中有一个 Importing 的选项,右侧 VM potions for importer 中添加 -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true 
        但是有时候好用有时候不怎么好用,第一种上来就可以了
    

