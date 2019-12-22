##### 一、 索引规范

* 联表查询时JOIN列的数据类型必须相同，并且要建立索引
* 选择区分度大的列建立索引。组合索引中，区分度大的字段放在最前
* 对字符串使用前缀索引  ，建议 不超过8个字符
* 不对过长的VARCHAR字段建立索引，优先考虑前缀索引，或添加CRC32或MD5伪列建立索引 
* 合理创建联合索引 ，（a,b,c）相当于(a) 、（a,b） 、(a,b,c)
* 合理使用覆盖索引减少的IO，避免排序。

##### 二、字符集

常见问题：

数据非法写入：update a.str_utf8 = b.str_utf8mb4

join时字段类型不匹配： a.str_utf8 = b.str_utf8mb4

字符集更改： alter table t1 charset utf8mb4 (只改后面写入的字符集) 与 alter table t1 convert to charset utf8mb4 (也改现有数据，建议)

##### 三、常见问题

* 为什么使用INNODB? 事务、行级锁、MVCC、  热备份、 安全 
* 如何存储IPV4 / IPV6?  使用UNSINGED INT 存储IP地址，可通过函数转化为ipv4/ipv6地址
* INT(1)  ? 显示字符宽度，非存储长度
* 建议在哪些列创建索引？ 覆盖索引和单列索引怎么选择？  按过滤性，过滤性强的单列索引即可，否则根据组合频率和过滤性选择
* 禁止使用存储过程、触发器等PLSQL且逻辑不要放入MySQL处理

##### 四、表字段设计

##### 1、分表策略

* 推荐使用HASH进行散表，表名后缀使用十进制数，数字必须从0开始
* 按时间日期分表需符合YYYYMMDDHH格式，如2017021301。年份必须4位数。例如按日散表user_20180202、按月散表user_201802
* 采用合适的分库分表策略。如千库十表、十库白表

##### 2、库表禁止使用项

* 禁止以非字母开头命名表名及库名
* 禁止使用分区表

##### 3、字段设计及类型选择

* 使用UNSIGNED存储非负数值
* 使用INT UNSIGNED存储IPV4
* 所有字段均定义为NOT NULL
* 用DECIMAL代替FLOAT和DOUBLE存储精确浮点数。例如与货币、金融相关的数据
* INT类型固定占4字节存储，INT(4)只代表显示字符宽度为4位，不代表存储长度

#####  4、索引（快速定位数据的一种数据结构）

索引：

idx_order_no_create_time (order_no , create_time);

select * from a1_test  where create_time =  '2018-03-03' and order_no ='8888' 这条sql会使用到该索引

select* from  a1_test where  create_time = '2018-03-03' 则不会使用到该索引

select * from a1_test where order_no in('8888','8889') order by create_time  desc 只是用到索引的前部分order_no,  使用了in,后部分order by 不会使用到索引create_time

覆盖索引： 一个索引包含所有要查询的字段的值（覆盖索引包括id）

##### 5、什么情况用不到索引

1】where条件中没有内容

2】否定条件： <>, not in,  not exists

3】join中连接字段类型（或字符集）不一致 

4】扫描内容超过全表的20%

5】where条件的字段存在函数运算

6】like '%name'

7】出现隐式字符类型转换

##### 6、Mysql优化思路

1、explain功能，打印执行计划

2、 推荐使用select count(*) from tb;

3、 将select * 转换为具体字段，有些查询就会用到覆盖索引

4、连接查询的join字段需添加索引（on两边的字段）

5、避免更新索引列值

6、在选择性高的字段添加索引

7、索引问题，有数据时使用show index from tb_name查看

9、能覆盖索引，尽量使用覆盖索引

10、order by 后面字段尽量也放在索引中(where xxx = 'xxx'而不是in)

##### 五、bad sql问题 

* 不能select/delete/update语句中无where条件或者条件范围较大
* delete from table_a 改写成 truncate table table_a
* select/update 全表或范围较大，和delete条件范围较大时需改成分段做，一定要有索引可走

* 避免使用子查询（子查询产生的临时表再扫描无所有可走）
* 不要在索引字段使用函数
* 避免like '%xxx%' ，使用like 'xxx%' , mysql是可以使用前缀索引扫描
* 在业务上想办法去掉 where status =0这样的查询，可以改成添加时间之类的小范围的条件，因为超过20%基本就全表扫描了

* 总之，使用短小快的sql

![1544079056580](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1544079056580.png)

![1544079192134](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1544079192134.png)

in和between字段的索引只有一个有用

join连接项两边和条件都需加索引

大海捞针索引 

子查询的where条件字段会产生锁，影响性能

二级索引表中含有主键字段

满足where所有条件后再对order字段加索引才有意义select id,name,adress,phone_no from table_a where status=0 and phone_no='13029837645'
and sex='male' order by salary desc;针对这个sql，应使用索引idx_phoneno_status_sex_salary(phone_no,status,sex,salary)



·select name,dt from table_a where phone_no='13029837645' order by dt;

应该建索引：dx_phoneno_address_name(phone_no,dt,name)，加name是实现覆盖索引



主键（聚集索引）的叶子节点包含所有 字段的值



什么情况用不到覆盖索引：

–select 字段过多
–前缀索引（767字节）
–text、blob字段存在

http://wiki.corp.qunar.com/confluence/pages/viewpage.action?pageId=226770403 