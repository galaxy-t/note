# Spring 重试

    https://mp.weixin.qq.com/s/x5Ps8SB7Gzr9fFYus2lxDw

    Spring 实现了一套重试机制, Spring Retry 是从 Spring Batch 独立出来的一个功能
    为了保证容错性, 可用性, 一致性等, 一般用来应对外部系统的一些不可预料的返回, 异常 等, 特别是网络延迟,中断等情况.

    @Configuration
    @EnableRetry
    public class Application {
    
        @Bean
        public RetryService retryService(){
            return new RetryService();
        }
    
        public static void main(String[] args) throws Exception{
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext("springretry");
            RetryService service1 = applicationContext.getBean("service", RetryService.class);
            service1.service();
        }
    }
    
    @Service("service")
    public class RetryService {
    
        @Retryable(value = IllegalAccessException.class, maxAttempts = 5,
                backoff= @Backoff(value = 1500, maxDelay = 100000, multiplier = 1.2))
        public void service() throws IllegalAccessException {
            System.out.println("service method...");
            throw new IllegalAccessException("manual exception");
        }
    
        @Recover
        public void recover(IllegalAccessException e){
            System.out.println("service retry after Recover => " + e.getMessage());
        }
    
    }

    @EnableRetry - 表示开启重试机制 
    @Retryable - 表示这个方法需要重试，它有很丰富的参数，可以满足你对重试的需求 
    @Backoff - 表示重试中的退避策略 
    @Recover - 兜底方法，即多次重试后还是失败就会执行这个方法

    Spring-Retry 的功能丰富在于其重试策略和退避策略，还有兜底，监听器等操作。