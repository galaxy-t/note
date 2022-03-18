# Spring 事务的传播机制

    事务的传播机制是针对两个不同的带事务的方法来看的
    假若现在有方法 a 调用方法 b ， 在 b 的 @Transactional 注解上使用 propagation 来设置其事务的传播行为
    如：@Transactional(propagation = Propagation.REQUIRED)
    
1. REQUIRED(默认)：如果 a 没有事务，若不存在事务则创建一个新事务，若当前存在事务则加入该事务

2. SUPPORTS：支持当前事务，如果当前存在事务，就加入该事务，如果当前不存在事务，就以非事务执行

3. MANDATORY：如果当前存在事务，就加入该事务，如果不存在则抛出异常

4. REQUIRES_NEW: 无论当前存不存在事务，都创建新事务

5. NOT_SUPPORTED：以非事务方式执行操作，如果当前存在事务，就把当前事务挂起

6. NEVER：以非事务方式执行，如果当前存在事务，则抛出异常

7. NESTED: 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与 REQUIRED 类似的操作

