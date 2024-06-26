    
    TiDB 是 PingCAP 公司受 Google Spanner / F1 论文启发而设计的开源分布式 HTAP (Hybrid Transactional and Analytical Processing) 数据库，
    结合了传统的 RDBMS 和NoSQL 的最佳特性。TiDB 兼容 MySQL，支持无限的水平扩展，具备强一致性和高可用性。
    TiDB 的目标是为 OLTP(Online Transactional Processing) 和 OLAP (Online Analytical Processing) 场景提供一站式的解决方案。
    
    TiDB 是一个与 MySQL 兼容的数据库，因此我们可以像使用 MySQL 一样使用它。
    由于 TiDB 的横向可扩展性，现在我们可以自由扩展我们的数据库，即使我们有超过一万亿的记录来应对。
    
1. 高度兼容 MySQL
   >大多数情况下，无需修改代码即可从 MySQL 轻松迁移至 TiDB，分库分表后的 MySQL 集群亦可通过 TiDB 工具进行实时迁移。
   
2. 水平弹性扩展
   > 通过简单地增加新节点即可实现 TiDB 的水平扩展，按需扩展吞吐或存储，轻松应对高并发、海量数据场景。
   
3. 分布式事务
   > TiDB 100% 支持标准的 ACID 事务。
   
4. 真正金融级高可用
   > 相比于传统主从 (M-S) 复制方案，基于 Raft 的多数派选举协议可以提供金融级的 100% 数据强一致性保证，且在不丢失大多数副本的前提下，可以实现故障的自动恢复 (auto-failover)，无需人工介入。

5. 一站式 HTAP 解决方案
   > TiDB 作为典型的 OLTP 行存数据库，同时兼具强大的 OLAP 性能，配合 TiSpark，可提供一站式 HTAP解决方案，一份存储同时处理OLTP & OLAP(OLAP、OLTP的介绍和比较 )无需传统繁琐的 ETL 过程。
   
6. 云原生 SQL 数据库
   > TiDB 是为云而设计的数据库，同 Kubernetes （十分钟带你理解Kubernetes核心概念 ）深度耦合，支持公有云、私有云和混合云，使部署、配置和维护变得十分简单。
　   TiDB 的设计目标是 100% 的 OLTP 场景和 80% 的 OLAP 场景，更复杂的 OLAP 分析可以通过 TiSpark 项目来完成。 TiDB 对业务没有任何侵入性，能优雅的替换传统的数据库中间件、数据库分库分表等 Sharding 方案。同时它也让开发运维人员不用关注数据库 Scale 的细节问题，专注于业务开发，极大的提升研发的生产力.
    
    