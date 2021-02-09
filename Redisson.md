    
    // 最简单常用的方式
    // 1. 设置一个锁 key
    String lockKey = "abcdtest";
    // 2. 通过这个 key 得到一把锁的实例
    RLock lock = this.redissonClient.getLock(lockKey);
    
            // 3. 得到锁的过程必须使用 try 包裹, 包括得到锁之后要执行的业务逻辑
            try {
                // 4.1 尝试得到锁, 允许等待时间为 10 秒, 等待的时间线程将被禁用, 不建议使用这种设置时间的方式
                //boolean lockGet = lock.tryLock(10, TimeUnit.SECONDS);
                // 4.2 尝试得到锁, 不设置等待时间, 只尝试一次即返回结果, 建议使用, 若获取不到可以直接返回给前端, 让前端提醒用户重试或进行其它操作
                //     若获取不到的结果一定是返回给前端, 由前端提醒用户进行重试, 那么多等待几秒, 浪费更多的内存显然是不合理的操作
                //     在一些特定的业务处理中或许要求进行等待, 酌情而定
                //     注: 该方法不会抛出异常, 但是开人人员依然应该遵守在 try 中获取锁, 在 finally 中释放锁的原则进行开发
                boolean lockGet = lock.tryLock();
    
                // 5. 如果得到了锁则进行业务处理
                if (lockGet) {
                    System.out.println("已经得到了锁");
                }
            } finally { // 6. 无论如何都要在 finally 中释放锁
                lock.unlock();
            }
    
            // 注: Redission 的 key 在 redis 中默认为 30 秒过期, 如果超过这个时间没有释放锁, 但是线程被终端, 那么锁会被主动释放
            //     如果超过 30 秒这个锁没被释放, 但是线程依然在运行, 那么这个 key 的过期时间会被延长 30 秒, 开发的时候一定要注意长时间占有锁的情况