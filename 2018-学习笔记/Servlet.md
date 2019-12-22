### Servlet&tomcat&jdbc

#### 一、Web应用加载过程：

context-param   >  listener(ServletContextListener等 ，哪个写在前面哪个先加载)  >   filter（哪个url-pattern在前先加载哪个，加载完加载servlet-name在前的）  > servlet(load-on-startup小的先加载，最迟用户访问 前加载)

#### 二、路径映射，转发，重定向

匹配顺序：

①全路径映射/aaa/bbb > ②路径映射/aaa/* > ③扩展映射 *.xxx开头 > ④默认映射 / 

转发：地址不变 ，只有一次请求 如： req.getRequestDispatcher("/b.html").forward(req,resp);

重定向：地址航会变, 两次请求如： resp.sendRedirect("/b.html"); resp.sendRedirect("http://www.baidu.com");

#### 三、cookie

Cookie cookie = new Cookie("key", value);

response.addCookie(cookie);

Cookie [] cookie = request.getCookies();

PrintWrite writer = resp.getWriter();

#### 四、Tomcat编码

#####  utf-8可变长编码

<Connector URIEncoding="UTF-8" useBodyEncodingForURI="true">设置为true对QueryString采用Body的编码

##### Post表单的编解码

在第一次调用request.getParameter之前设置编码：request.setCharacterEncoding("UTF-8")

未设置按照浏览器中<meta... >规定的编码方式

JDBC url指定：

url = "jdbc:mysql://localhost:3306/DB?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true"

#### 五、JDBC，java数据库连接

##### jdbc的 api

Driver：所有jdbc驱动器必须实现此接口

**DriverManager:**

registerDriver(Driver driver)

getConnection(String url,String user,String pw)

**Connection:**

getMetaData()

createStatement()

prepareStatement(String sql)

**Statement**

boolean execute(string sql)  true表示有结果集

int executeUpdate(String sql)

ResultSet executeQuery(String sql)

**PreparedStatement继承自Statement，推荐使用**

好处：1、sql参数化，简化程序  2、一次编译多次执行，提升性能 3、防止常见sql注入问题

如： String sql = "xxxxx  where name = ?";

PrepareStatement statement = conn.prepareStatement(sql);

statement.setString("test");

ResultSet rs=statement.executeQuery();

**sql注入问题**

如"select * from t1 where name" + name+";“

用户填入 name = "1 ' or '1' = '1" ,则恒真

**ResultSet**

getXXX(int index / String columnName),XXX可为String,Int,Float,Index/column

JDBC程序开发步骤：加载驱动器类（registerDriver省略,用class.forname）,建立与数据库的连接，创建statement对象，执行sql语句，访问 ResultSet的记录，依次关闭ResultSet，Statement,Connection

##### jdbc事务基础

原子性：不可 分割的工作单位  

一致性：使数据库从一个状态 变到另一个状态，如a给b转账，不会a少100块，b钱不变

隔离性 ：  事务执行不能被其他事务干扰，如a,b给c转账，a事务和b事务同时提交，有一个事务需等待，这条数据独占锁定状态

持久性：事务一旦提交，数据改变是永久的

**jdbc事务是默认自动提交的**

* setAutoCommit(boolean autoCommit)
* commit()
* rollback()



