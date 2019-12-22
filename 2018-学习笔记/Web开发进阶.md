#### 构建高可靠、高性能的web应用

目标：构建**高可靠、高性能的web应用**，这是接下来学习的一些计划。

##### 一、可靠性：可扩展性、服务降级、负载均衡

增加可靠性：**①隔离/解耦 ②限流（漏桶、令牌桶）③熔断（Hystrix,dubbo） ④服务降级 ⑤灰度发布**

垂直扩展（scala up）：提高机器硬件配置，如加内存等

水平扩展：加机器，服务须无状态，可分布式

数据库扩展sharding：

--垂直拆分(一个库数据量太大，表间将关联密切的表划分在一起)：

如User Order库拆成 ->User 、 Order

--水平拆分（一个表的数据量太大，一表拆多表，根据查询使用过情况确定规则,单表数据不要超过1000万，做好索引）:

如Order表根据 -hash , -代理商拆分

##### 二、性能：缓存、并发、池化、异步

①负载均衡

Ngin反向代理服务器：http转发,一个请求对应多台机器

策略：

Random，随机打到哪台机器

RoundRobin，轮询

LeastActive，如果一个机器A处理请求返回慢，可能就把请求打到另一台机器B

IPHash， Hash策略 ，如根据hash做hash，特定用户打到特定机器，

ConsistentHash，如根据内容做hash，有缓存的请求打到 同一台机器
健康检查：

Healthcheck.html， Nginx定期检查Healcheck.html，如不能查到说明这机器挂了

②服务降级

对提供的服务进行分级，核心 服务具有更高的优先级，动态地关闭某个功能等

功能开关：全流程开关, 如去哪儿的qconfig

③并发-Servlet

容易出现的 问题 ：

* count++;语句是线程不安全的

* 在多线程环境下如servlet中的全局变量定义使用HashMap，可以使用ConcurrentHashMap()或者Collections.unmodifiableMap(new HashMap() )来进行安全的读（get）操作,在doGet或doPost定义的本地map是线程安全的

④并发-锁

低并发推荐使用 AtomicInteger、AtomicLong

synchronized， 一直等。ReentrantLock：可重入锁、互斥，它有timeout机制

读写锁：ReadWriteLock 读锁是共享锁，写锁是排它锁互斥的，读写锁有级别，获取到读锁的线程不能升级成写锁，能降级，写锁能获取到读锁，适合读多写少

乐观锁：分布式锁 ？

悲观锁：

⑤缓存-本地

HashMap

ConcurrentHashMap

用户的搜索结果进行缓存等，可以用Guava Cache，设置过期策略

memcached集群, 使用key-value 如 username_zhangsan  ->  {"username:zhangsan",  "nickname":"二哈"}

LRU: 近期最少使用

分布式 ： 一致性Hash,  client实现

Client :  xmemcached 、spymemcached

⑥缓存-redis

丰富的数据结构，可持久化： Hash , List, Set , SortedSet(给set中的每个元素给个score分，如实现分布式 优先级队列)

⑦序列化

方式有：

* Json (fastjson或者jackson)
* Java serialization  （记得显示指定serializeVersionID?）
* Hessian (dubbo使用)

dubbo中序列化不要使用枚举类，否则枚举变了服务更新不同步

⑧池化技术，复用资源

* 线程池： Executor
* 连接池： tomcat-jdbc、dbcp，c3p0，druid
* 对象池：spring针对对象的管理其实是一个对象池，又比如说A-B, A-C, A-D可以建个A的对象池，节约内存

⑨性能-异步

--前端轮询、后端异步，如做一个搜索系统，搜索很耗时，不能同步地等待应使用异步，但异步要控制好流量，别把别人的服务请求崩了

​      Futrue/CountDownLatch

消息队列，消息一定要被消费，qmq使用推的方式，kafka只存下消息，让客户端自己来消费

​     QMQ/Kafka/AMQ/rabbitmq

HTTP

​     async-http-client

​     Apache HttpComponents

Dubbo

​     异步调用、参数回调



#### 代码的坏味道

一致性hash，如加了2台机器，让哪些数据打到新机器？增加缓存命中成功概率，减少查数据库



高并发导致数据库死锁或相应慢怎么解决？

①、如商户收款，划分为几个子账户，如均等划分、按一定比例划分、按支付请求金额划分等，思想分而治之

②、分布式集群



#### web安全

验证码失效

通过cookie确认身份，禁止取cookie中的username作为标志