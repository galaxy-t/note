# Sentinel

    限流, 熔断
    官网: https://sentinelguard.io/zh-cn/
    SpringCloudALibaba 官网: https://sca.aliyun.com/

## 安装客户端

    1. 下载 sentinel-dashboard-1.8.7.jar 包
    2. 启动命令: java -Dserver.port=8999 -Dcsp.sentinel.dashboard.server=localhost:8083 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.7.jar
    3. 浏览器访问: http://localhost:8999/, 默认账号密码均为 sentinel

## 使用

    1. 项目添加依赖如下

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>

    2. 添加配置如下: 

        server:
            spring:
                application:
                    name: test-web2
                profiles:
                    active: dev
                cloud:
                    nacos:
                        username: 'nacos'
                        password: '17784574.'
                        discovery:
                            server-addr: 121.43.144.125:8848
                    sentinel:       # 配置
                        transport:
                            port: 8998  # 像 Dashboard 客户端发送数据的端口
                            dashboard: 'localhost:8999' # Dashboard 客户端地址


    3. 在需要限流或熔断的接口上添加注解: @SentinelResource("hello")

    4. 使用Dashboard 添加限流和熔断规则, 名称为 hello, 即可对该接口进行限流或熔断

    5. 可以将规则放到 nacos 中作动态配置

    6. 调用方也可以进行限流, 需要在 RedisTemplate 创建方法上使用注解如下
        @SentinelRestTemplate(blockHandler = "handleBlock", blockHandlerClass = SentinelHandler.class, fallback = "fallback", fallbackClass = SentinelHandler.class)
        其中: 限流(blockHandler, blockHandlerClass), 降级(fallback, fallbackClass)
    
    7. 创建 SentinelHandler 类, 其中有两个静态方法如下

        /**
         * 限流
         *
         * @param request
         * @param body
         * @param execution
         * @param exception
         * @return
         */
        public static ClientHttpResponse handleBlock(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException exception) {
    
            log.info("被限流了: {}", body);
    
            log.error("被限流了: ", exception);
    
            return new SentinelClientHttpResponse("被限流了");
        }
    
        /**
         * 降级
         *
         * @param request
         * @param body
         * @param execution
         * @param exception
         * @return
         */
        public static ClientHttpResponse fallback(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException exception) {
    
            log.info("被降级了: {}", body);
    
            log.error("被降级了: ", exception);
    
            return new SentinelClientHttpResponse("被降级了");
        }

        此时再使用 RestTemplate 就可以做到客户端限流和熔断了
