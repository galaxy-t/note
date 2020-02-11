# Spring Cloud Gateway

    Spring Cloud Gateway是Spring Cloud官方推出的第二代网关框架，用于取代Zuul网关。
    
    网关作为一个系统的流量的入口，有着举足轻重的作用，通常的作用如下：
    
    1：协议转换，路由转发
    2：流量聚合，对流量进行监控，日志输出
    3：作为整个系统的前端工程，对流量进行控制，有限流的作用
    4：作为系统的前端边界，外部流量只能通过网关才能访问系统
    5：可以在网关层做权限的判断
    6：可以在网关层做缓存
    
    与 Zuul 就不进行对比了，为啥用它？它支持 WebSocket。
    
    客户端向Spring Cloud Gateway发出请求。 
    如果Gateway Handler Mapping确定请求与路由匹配（这个时候就用到predicate），
    则将其发送到Gateway web handler处理。 Gateway web handler处理请求时会经过一系列的过滤器链。
    过滤器有 pre（代理请求之前），post（代理服务响应之后）两种
    
### Predicate(路由断言)

    Predicate来自于java8的接口。Predicate 接受一个输入参数，返回一个布尔值结果。
    该接口包含多种默认方法来将Predicate组合成其他复杂的逻辑（比如：与，或，非）。
    可以用于接口请求参数校验、判断新老数据是否有变化需要进行更新操作。add–与、or–或、negate–非。
    
    Spring Cloud Gateway 提供了很多种 Predicate ， 用于对路由的不同处理
    
1. Path Route Predicate Factory(比较常用的)

    `
    spring:
      cloud:
        gateway:
          routes:
            - id: path_route        
              uri: http://localhost:8081    //目标服务地址
              order: 0
              predicates:
                - Path=/foo/**          //请求路径
              filters:
                - StripPrefix=1         //去除第一个路径，第一个路径用于区分访问哪个服务，但是具体服务里面应该是直接端口号加路径
    `
   
   `
    以上述例子为例，假设该网关端口号为 8080
    请求：http://localhost:8080/foo/bar
    路由会请求到：http://localhost:8081/bar
   ` 
   
### Filter

    由filter工作流程点，可以知道filter有着非常重要的作用，
    在“pre”类型的过滤器可以做参数校验、权限校验、流量监控、日志输出、协议转换等，
    在“post”类型的过滤器中可以做响应内容、响应头的修改，日志的输出，流量监控等。
    
    
    在Spring Cloud Gateway中，filter从作用范围可分为另外两种，
    一种是针对于单个路由的gateway filter，它在配置文件中的写法同predict类似；
    另外一种是针对于所有路由的global gateway filer。
   
1. gateway filter

    

    
    
    


