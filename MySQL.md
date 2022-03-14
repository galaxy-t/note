# MySQL


---

    SELECT u.real_name,COUNT(h.id) AS amount
    FROM t_user u LEFT JOIN t_business_huoke h ON u.id = h.broker_id
    WHERE h.create_time BETWEEN '' AND ''
    GROUP BY u.id
    ORDER BY amount DESC

---

### 解压版安装

1. 下载
    
    > mysql-8.0.14-winx64.zip 下载之后大概是这个名字
             
    > 解压后放在我的是放在    D:\tools\mysql-8.0.14-winx64

2. 配置环境变量

    > 在系统变量的 Path 中添加     D:\tools\mysql-8.0.14-winx64\bin

3. 配置初始化的my.ini文件

    > 在 D:\tools\mysql-8.0.14-winx64 下新建 my.ini 文件然后将下面内容复制进入
    `
        [mysqld]
        # 设置3306端口
        port=3306
        # 设置mysql的安装目录
        basedir=D:\\tools\\mysql-8.0.14-winx64
        # 设置mysql数据库的数据的存放目录
        datadir=D:\\tools\\mysql-8.0.14-winx64\\data
        # 允许最大连接数
        max_connections=200
        # 允许连接失败的次数。这是为了防止有人从该主机试图攻击数据库系统
        max_connect_errors=10
        # 服务端使用的字符集默认为UTF8
        character-set-server=utf8
        # 创建新表时将使用的默认存储引擎
        default-storage-engine=INNODB
        # 默认使用“mysql_native_password”插件认证
        default_authentication_plugin=mysql_native_password
        [mysql]
        # 设置mysql客户端默认字符集
        default-character-set=utf8
        [client]
        # 设置mysql客户端连接服务端时默认使用的端口
        port=3306
        default-character-set=utf8
    `
    > 注意 basedir 和 datadir 一定要是两个斜杠, data 文件夹要手动创建

4. 初始化数据库

    > 开始菜单搜索 cmd ,以管理员身份运行 , 进入到 D:\tools\mysql-8.0.14-winx64\bin
    
    > 执行 mysqld --initialize --console

    > 仔细看里面会提示有初始化密码 , 记得保存下来

5. 安装服务
    
    > mysqld --install  [服务名]   服务名可以随便起,我起的是 mysql8

6. 启动服务

    > net start [服务名]

7. 停止服务

    > net stop [服务名]

8. 卸载服务

    > sc delete  [服务名]

9. 注: 启动,停止,卸载 服务可以再 DOS 的任何路径的命令窗口都可以执行,因为安装根目录中的 bin 已经加入到 Path 环境变量中了

10. 更改用户密码

    > DOS 窗口执行命令 mysql -u root -p
              
    > 输入第四步保存下来的初始化的 root 密码进入到 mysql 控制台

    > ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456';

    > 执行上面的 sql 语句将 root 密码修改为 123456
---

### 事务

1. 四要素（ACID）

    > 原子性（Atomicity）：要么全部完成，要么全部不完成
    
    > 一致性（Consistency）：一个事务单元需要提交之后才会被其他事务可见；
    
    > 隔离性（Isolation）：并发事务之间不会互相影响，设立了不同程度的隔离级别，通过适度的破坏一致性，得以提高性能；
    
    > 持久性（Durability）：事务提交后即持久化到磁盘不会丢失。
    
2. 事务并发存在的问题

    > 脏读（dirty read）: 一个事物读取到了另一个未提交的事务的修改
    
    > 不可重复读（unrepeatable read）: 一个事务读取到了另一个事务已提交的事务的修改
    
    > 幻读（phantom read）： 跟不可重复读很类似 ， 都可以简单理解为 ， 
    > 同样的条件，第一次和第二次读出来的记录数不一样 ，区别在于 , 
    > 不可重复读是两次读取同一条数据得到不同的结果 ，而幻读是两次读取同一个范围得到不同的结果
    > 这种说法其实只是便于理解，但并不准确，因为可能存在另一个事务先插入一条记录然后再删除一条记录的情况，
    > 这个时候两次查询得到的记录数也是一样的，但这也是幻读，所以严格点的说法应该是：两次读取得到的结果集不一样）
    > 不可重复读是因为其他事务进行了 UPDATE 操作，幻读是因为其他事务进行了 INSERT 或者 DELETE 操作
    
    > 丢失更新（lost update）: 一个事务的提交会覆盖之前事务提交的结果 ， 或者一个事务的回滚会覆盖之前事务提交的结果
    
3. 隔离级别

    > 读未提交（Read Uncommitted）：可以读取未提交的记录，会出现脏读，幻读，不可重复读，所有并发问题都可能遇到
    
    > 读已提交（Read Committed）：事务中只能看到已提交的修改，不会出现脏读现象，但是会出现幻读，不可重复读；（大多数数据库的默认隔离级别都是 RC，但是 MySQL InnoDb 默认是 RR）
    
    > 可重复读（Repeatable Read）：MySQL InnoDb 默认的隔离级别，解决了不可重复读问题，但是任然存在幻读问题；（MySQL 的实现有差异，后面介绍）
    
    > 序列化（Serializable）：最高隔离级别，啥并发问题都没有。
    
---

### 索引

1. 优点

    > 所有的MySql列类型(字段类型)都可以被索引，也就是可以给任意字段设置索引
    
    > 大大加快数据的查询速度
    
2. 缺点

    > 创建索引和维护索引要耗费时间，并且随着数据量的增加所耗费的时间也会增加
    
    > 索引也需要占空间，我们知道数据表中的数据也会有最大上线设置的，如果我们有大量的索引，索引文件可能会比数据文件更快达到上线值
    
    > 当对表中的数据进行增加、删除、修改时，索引也需要动态的维护，降低了数据的维护速度。
    
3. 使用原则

    > 对经常更新的表就避免对其进行过多的索引，对经常用于查询的字段应该创建索引
    
    > 数据量小的表最好不要使用索引，因为由于数据较少，可能查询全部数据花费的时间比遍历索引的时间还要短，索引就可能不会产生优化效果。
    
    > 在一同值少的列上(字段上)不要建立索引，比如在学生表的"性别"字段上只有男，女两个不同值。相反的，在一个字段上不同值较多可以建立索引。
    
4. 分类

    > 唯一索引 ： 索引列中的值必须是唯一的，但是允许为空值
    
    > 主键索引：是一种特殊的唯一索引，不允许有空值。
    
    > 联合索引: 在表中的多个字段组合上创建的索引，只有在查询条件中使用了这些字段的左边字段时，索引才会被使用，使用组合索引时遵循最左前缀集合。
    > 最左原则：假如有一个组合索引的字段为（a , b , c），当条件里为(a)(a , b)(a , c)时都能够使用到该索引 ， 不限制条件顺序 ， 即（b , a）(c , a) 都是可以的
    > mysql查询优化器会判断纠正这条sql语句该以什么样的顺序执行效率最高，最后才生成真正的执行计划 ， 但写的时候还是尽量按照顺序来写查询条件
    > 但是如果查询条件里不存在 （a）那就不能够用上该索引 ， 这跟 B+ 树的算法有关系
    
    > 全文索引： 只有在MyISAM引擎上才能使用，只能在CHAR,VARCHAR,TEXT类型字段上使用全文索引 ， 搜索的关键字默认至少要4个字符 ， 
    > 在使用全文搜索时，需要借助MATCH函数 , 如： SELECT * FROM t4 WHERE MATCH(info) AGAINST('gorlr');
    > http://blog.sina.com.cn/s/blog_ae1611930101a063.html
    
    > 空间索引: 空间索引是对空间数据类型的字段建立的索引 ， 具体能干嘛我也不知道，可能跟游戏开发有关，可能跟别的东西有关 ， 略略略
    
5. 存储类型

    > BTREE:
    > B-TREEB-TREE以B+树结构存储数据，大大加快了数据的查询速度
    > B-TREE索引在范围查找的SQL语句中更加适合（顺序存储）
    > B-TREE索引使用场景: 
    > 全值匹配的查询SQL，如 where act_id= '1111_act'
    > 联合索引汇中匹配到最左前缀查询，如联合索引 KEY idx_actid_name(act_id,act_name) USING BTREE，只要条件中使用到了联合索引的第一列，就会用到该索引，但如果查询使用到的是联合索引的第二列act_name，该SQL则便无法使用到该联合索引（注：覆盖索引除外）
    > 匹配模糊查询的前匹配，如where act_name like '11_act%'
    > 匹配范围值的SQL查询，如where act_date > '9865123547215'（not in和<>无法使用索引）
    > 覆盖索引的SQL查询，就是说select出来的字段都建立了索引
    
    > HASH: 
    > Hash索引基于Hash表实现，只有查询条件精确匹配Hash索引中的所有列才会用到hash索引
    > 存储引擎会为Hash索引中的每一列都计算hash码，Hash索引中存储的即hash码，所以每次读取都会进行两次查询
    > Hash索引无法用于排序
    > Hash不适用于区分度小的列上，如性别字段
    
---

### 优化

1. 尽量避免全表扫描 ，应该考虑 where 和 order by 建立索引

2. 应尽量避免在 where 子句中对字段进行 null 值判断，否则将导致引擎放弃使用索引而进行全表扫描

    `如：select id from t where num is null`

    > 最好不要给数据库留NULL，尽可能的使用 NOT NULL填充数据库. 
    > 备注、描述、评论之类的可以设置为 NULL，其他的，最好不要使用NULL。
    > 不要以为 NULL 不需要空间，比如：char(100) 型，在字段建立时，空间就固定了， 不管是否插入值（NULL也包含在内），都是占用 100个字符的空间的，如果是varchar这样的变长字段， null 不占用空间。
    
    > 可以在num上设置默认值0，确保表中num列没有null值，然后这样查询：
    `select id from t where num = 0`
    
    > 应尽量避免在 where 子句中使用 != 或 <> 操作符，否则将引擎放弃使用索引而进行全表扫描
    
    > 应尽量避免在 where 子句中使用 or 来连接条件，如果一个字段有索引，一个字段没有索引，将导致引擎放弃使用索引而进行全表扫描，如：
    `select id from t where num=10 or Name = 'admin'`
    > 可以这样查询：
    `select id from t where num = 10
     union all
     select id from t where Name = 'admin' `
     > in 和 not in 也要慎用，否则会导致全表扫描，如：
     `select id from t where num in(1,2,3)`
     > 对于连续的数值，能用 between 就不要用 in 了：
     `select id from t where num between 1 and 3`
     > 很多时候用 exists 代替 in 是一个好的选择：
     `select num from a where num in(select num from b)`
     > 用下面的语句替换：
     `select num from a where exists(select 1 from b where num=a.num)`
     
     > 下面的查询也将导致全表扫描：
     `select id from t where name like ‘%abc%’`
     > 若要提高效率，可以考虑全文检索。
     
     > Update 语句，如果只更改1、2个字段，不要Update全部字段，否则频繁调用会引起明显的性能消耗，同时带来大量日志。
     
     > select count(*) from table；这样不带任何条件的count会引起全表扫w描，并且没有任何业务意义，是一定要杜绝的。
     
     > 索引并不是越多越好，索引固然可以提高相应的 select 的效率，但同时也降低了 insert 及 update 的效率，因为 insert 或 update 时有可能会重建索引，所以怎样建索引需要慎重考虑，视具体情况而定。一个表的索引数最好不要超过6个，若太多则应考虑一些不常使用到的列上建的索引是否有 必要。
     
     > 尽量使用数字型字段，若只含数值信息的字段尽量不要设计为字符型，这会降低查询和连接的性能，并会增加存储开销。这是因为引擎在处理查询和连 接时会逐个比较字符串中每一个字符，而对于数字型而言只需要比较一次就够了。
     
     > 尽可能的使用 varchar/nvarchar 代替 char/nchar ，因为首先变长字段存储空间小，可以节省存储空间，其次对于查询来说，在一个相对较小的字段内搜索效率显然要高些。
     
     > 任何地方都不要使用 select * from t ，用具体的字段列表代替“*”，不要返回用不到的任何字段。
     
     > 尽量避免大事务操作，提高系统并发能力。
     
     > 尽量避免向客户端返回大数据量，若数据量过大，应该考虑相应需求是否合理。
     

### 分解关联查询的优势

1. 让缓存的效率更高, 增加缓存的使用面积
2. 降低笛卡尔积, 相当于在应用中实现了哈希关联, 而不是使用 MySQL 的嵌套环关联
3. 查询本身效率也可能会有所提升
4. 分解后执行单个查询可以减少锁的竞争
5. 可以减少冗余记录的查询
6. 在应用层做关联, 可以更容易对数据库进行拆分, 更容易做到高性能和可扩展


---

### EXPLAIN 查看执行计划 

    -- 实际SQL，查找用户名为Jefabc的员工
    select * from emp where name = 'Jefabc';
    -- 查看SQL是否使用索引，前面加上explain即可
    explain select * from emp where name = 'Jefabc';
    
    expain出来的信息有10列，分别是id、select_type、table、type、possible_keys、key、key_len、ref、rows、Extra
    
    概要描述：
    id:选择标识符
    select_type:表示查询的类型。
    table:输出结果集的表
    partitions:匹配的分区
    type:表示表的连接类型
    possible_keys:表示查询时，可能使用的索引
    key:表示实际使用的索引
    key_len:索引字段的长度
    ref:列与索引的比较
    rows:扫描出的行数(估算的行数)
    filtered:按表条件过滤的行百分比
    Extra:执行情况的描述和说明
    
    参考： https://www.cnblogs.com/tufujie/p/9413852.html
    

