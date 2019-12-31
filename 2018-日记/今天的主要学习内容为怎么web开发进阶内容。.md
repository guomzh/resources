今天的主要学习内容为怎么web开发进阶内容。

工作内容：

1、了解dubbo，为明天的web考试做准备。

2、线上课程《web开发进阶》，主要了解了如何构建高可靠、高性能的web应用，和应用性能优化方面的知识。

​     学习体会： http://wiki.corp.qunar.com/confluence/pages/viewpage.action?pageId=228489174

3、线下课程《web开发安全》

4、java反射机制和使用小组分享： http://wiki.corp.qunar.com/confluence/pages/viewpage.action?pageId=228482578

心得体会：

1、使用Futrue/CountDownLatch进行异步操作提高系统性能，控制好流量，不能把一个的服务请求崩了

2、future.get() 要尽量指定timeout参数，防止过长时间阻塞

3、通过cookie确认身份，禁止只取cookie中的username作为标志

4、多使用池化技术，如线程池Executor、连接池 tomcat-jdbc、dbcp，c3p0，druid，对象池去复用资源

遇到的问题：

1、平时代码应注意检查一些基本规范，了解一些qunar的solr规则，避免一些代码的坏味道。



