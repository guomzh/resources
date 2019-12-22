### MVC

#### 1、定义：

dispatcherservlet -> 页面控制器 -> 返回模型数据（ModeAndView） ->  生成视图 -> 返回给前端控制器response

#### 2、开发步骤：

pom.xml  ->  web.xml（配置前端控制器DispatcherServlet） -> spring-mvc.xml -> src/main/java(Controller) -> src/main/webapp(开发视图页面jsp、vm、ftl....一般用json传数据) -> src/test/java(单测)

 ##### ①pom

![1545013043839](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1545013043839.png)

推荐配置tomcat或jetty插件，

使用mvn clean tomcat7:run 直接运行

##### ⑤web.xml

![1545013192744](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1545013192744.png)

##### ⑥上下文Context

DispatcherServlet初始化mvc上下文（web层，如Controller,HandlerMapping、HandlerAdapter、ViewResolver）， 是子容器继承自父容器，可以有多个；ContextLoadListener初始化DataSource、Dao、Servicce等非web层的基础是父容器。

##### ⑦mvc.xml等基础

![1545013913292](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1545013913292.png)

![1545014298201](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1545014298201.png)

##### ⑧rest风格的Controller

![1545014453007](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1545014453007.png)

##### ⑨mvc开发的单元测试-junit (针对controller层，service层作为mock)

![1545014710557](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1545014710557.png)

##### 



