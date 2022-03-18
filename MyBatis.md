# MyBatis

## 缓存

    MyBatis 对缓存提供支持
    一级缓存默认开启，并且是 session 级别，它的作用域为 SqlSession
    二级缓存，相对于一级缓存，二级缓存的作用域更广泛，它不止局限于一个 SqlSession，它可以在多个 SqlSession 之间共享，事实上它的作用域是 namespace， 
    mybatis 的二级缓存也是默认开启的，但由于它的作用域是 namespace，所以还需要在 mapper.xml 中开启才能生效
    
    一般使用 MyBatis 的时候肯定是会使用 Spring或SpringBoot，而 spring 中同一个事务使用同一个 SqlSession

### 缓存的优先级

    通过 mybatis 发起的查询，作用顺序为：二级缓存 -》 一级缓存 -》 数据库，其中任何一个环节查到不为空的数据，都将直接返回结果

### 缓存失效

1. 当在一个缓存作用域中发生了 update，insert，delete 动作后，将会触发缓存失效，下一次查询将命中数据库，从而保证不会查到脏数据
2. 未开启 Spring 事务


### 如果 mybatis 整合 spring

1. 未开启事务，则 mybatis 的一级缓存失效，每次查询都会关闭旧的 sqlsession，而创建新的，因为一级缓存基于 SqlSession，只要 SqlSession 不关闭，
    其缓存就一直存在
2. 在开启事务的情况下，spring 使用 ThreadLocal 获取当前资源绑定同一个 SqlSession，因此此时一级缓存是有效的

### 二级缓存

    默认情况下，mybatis 打开了二级缓存，但它并未生效，因为二级缓存的作用域是 namespace，所以还需要在 Mapper.xml 中配置一下才能使二级缓存生效

    在 mapper 中使用 <cache><cache/> 标签开启当前 namespace 的二级缓存
    <!--开启本mapper的namespace下的二级缓存-->
    <!--
        eviction:代表的是缓存回收策略，目前MyBatis提供以下策略。
        (1) LRU,最近最少使用的，一处最长时间不用的对象
        (2) FIFO,先进先出，按对象进入缓存的顺序来移除他们
        (3) SOFT,软引用，移除基于垃圾回收器状态和软引用规则的对象
        (4) WEAK,弱引用，更积极的移除基于垃圾收集器状态和弱引用规则的对象。这里采用的是LRU，
                移除最长时间不用的对形象

        flushInterval:刷新间隔时间，单位为毫秒，这里配置的是100秒刷新，如果你不配置它，那么当
        SQL被执行的时候才会去刷新缓存。

        size:引用数目，一个正整数，代表缓存最多可以存储多少个对象，不宜设置过大。设置过大会导致内存溢出。
        这里配置的是1024个对象

        readOnly:只读，意味着缓存数据只能读取而不能修改，这样设置的好处是我们可以快速读取缓存，缺点是我们没有
        办法修改缓存，他的默认值是false，不允许我们修改
    -->
    <cache eviction="LRU" flushInterval="100000" readOnly="true" size="1024"/>


## 多数据源

    在多数据源情况下，如果未添加事务，是能够完成切换数据源的，但是一旦加了事务，在获取 SqlSession 的时候会从 SqlSessionHolder 里面获取，
    也就是说，获取到的 SqlSession 是上一个使用过的 session，因为 connection 也不是空的，因此不会去执行 openConnection（）这个操作，
    所以切换数据源就失效了

    可以理解为：事务是要在同一个 SqlSession 里面才能保证原子性

### 多数据源事务处理方式

1. 利用 Spring 事务的传播方式： @Transactional(propagation = Propagation.REQUIRES_NEW)，如果在同一个事务里面，SqlSession 会延用同一个，
    导致 connection 不会切换，因此无法切换数据源，如果在内层使用事务，那么就会当作另一个新的事务，也就会重新获取连接，而且在内层方法里面如果抛出异常，
    也会导致外层事务回滚。
    但是如果内层事务执行完成，外层再抛出异常，则内层事务无法回滚，因为内层事务已经提交了。
    因此，如果要使用这种方式，需要将内层事务逻辑放在最后执行。
2. 本地分布式事务 atomikos 保证事务的进行