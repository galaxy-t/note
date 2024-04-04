# Nacos

    配置中心, 注册中心

    官网: https://nacos.io/
    SpringCloudALibaba 官网: https://sca.aliyun.com/

## 安装/启动/关机

    1. 去官网下载一个合适的版本下载, 然后在服务器解压(CentOS)
    2. 启动命令: sh startup.sh -m standalone
    3. 关机命令: sh shutdown.sh

## 开启用户名密码登录

    1. vim /conf/application.properties
    2. 设置 nacos.core.auth.enabled=false 为 true
    3. nacos.core.auth.server.identity.key=nacos  # 集群模式下 nacos 的验证
    4. nacos.core.auth.server.identity.value=123456  # 集群模式下 nacos 的验证
    5. nacos.core.auth.plugin.nacos.token.secret.key=SecretKey012345678901234567890123456789012345678901234567890123456789  # 用于生成 JWT 的令牌, 推荐将配置项设置为Base64编码的字符串，且原始密钥长度不得低于32字符

## 配置中心

### 服务端

    1. 命名空间, 可以根据需要自行创建, 用于对配置进行一定程度的隔离, 如无必要可以只使用默认的 PUBLIC 命名空间, 这个可以以项目名称作为创建命名空间的依据
    2. 在配置列表点击创建配置, 创建一个配置文件, 选择所属命名空间, 输入 Data ID(可以理解为这个配置文件的名称, 如: order-dev.properties, 含义为订单模块开发环境类型为 properties 的配置文件),
       输入 Group, 这个可以自行定义, 配置格式选择 properties, 内容的写法与项目中 properties 一致, 然后点击发布即可

### 客户端

    强烈推荐使用 springcloud 方式
    1. 首先最外层使用的 spring boot 版本如下

        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>3.0.2</version>
            <relativePath/>
        </parent>

    2. 依赖 spring cloud 和 spring cloud alibaba 如下

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2022.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2022.0.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

    3. 在需要的项目中依赖如下

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

    4. application.yml 配置如下
        spring:
            application:
                name: test-web  # 必须配置
            profiles:
                active: dev     # 可以省略, 但建议配置
            config:
                import: nacos:test-web-dev.properties   # 这个配置一定要有, 否则启动报错, 实际上这个名字就是 {spring.application.name}-{spring.profiles.active}.{spring.cloud.nacos.config.file-extension} 拼接之后的结果
            cloud:
                nacos:
                    # 用户名和密码也为配置中心和注册中心单独配置(隔离开来)
                    username: 'nacos'           # 登录用户名
                    password: '17784574.'       # 登录密码
                    discovery:
                        server-addr: 121.43.144.125:8848    # 注册中心配置
                        # 默认为临时实例, 设置为 false 代表该实例是永久实例
                        ephemeral: false
                    config:
                        import-check:
                            nabled: false # 这个配置一定要有, 否则启动报错
                        server-addr: 121.43.144.125:8848
                        file-extension: properties  # 指定配置文件类型, 建议使用 properties, 否则如使用 yaml 时候, 客户端 @Value("${test.name}") 会报错, 需要写成 : 分隔的形式

    5. 基本上其它所有的配置都可以放到配置中心去, 启动的时候会拉取下来然后被使用
    6. 如果某个 Bean 中的配置需要被更新则需要在这个类中使用注解 @RefreshScope , 否则配置中心修改发布后客户端不会被自动更新

## 注册中心

### 客户端

    1. 配置方式与配置中心基本一致, 只不过注册中心所依赖的 jar 包为

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
    
    2. 使用按照习惯(推荐 RestTemplate + LoadBalancer), 所需依赖如下

        <!-- 使用 @LoadBalanced 注解必须提供该库 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-loadbalancer</artifactId>
        </dependency>

    3. 配置 RestTemplate

        @Bean
        @LoadBalanced   // 添加这个注解, 会优先从注册中心匹配
        public RestTemplate restTemplate() {
    
            return new RestTemplate();
        }

    4. 接口调用
        
        // 第一个参数, test-web2 为项目的 spring.application.name, 也是在注册中心服务的名称, 后面的为 api 的路径
        String s = restTemplate.getForObject("http://test-web2/test/1?name=" + name, String.class);

    # 注: 当一个服务启动过并且是临时实例的时候, 将其修改为永久实例启动会被拒绝, 需要手动停止 nacos, 然后去 nacos/data/protocol/raft 目录下清空该目录下的内容然后再重启

    

