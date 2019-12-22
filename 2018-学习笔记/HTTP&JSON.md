### HTTP&JSON

#### 一、基础信息

* http报文格式： 起始行   首部   实体，如：

```
请求报文：
起始行
POST /index HTTP/1.1 或 GET /index?param=hello HTTP/1.1
首部字段
Host:www.qunar.com
Content-Type:application/x-www-form-urlencoded
Content-Length:61
实体
userName=san.zhang&userToken=b261e27db45684cb7c2fe8e084d37dab
```

```
响应报文格式：
起始行
 HTTP/1.1 200 OK
首部字段
Content-Type:application/json
Content-Length:20
实体
{"name":"san.zhang"}
```

* 如何区分不同的资源类型：  响应首部的content-type标志imie的资源类型

* http返回状态码表达的含义：

2XX类状态码信息表示：服务器成功的接收了客户端请求

3XX类状态码信息表示：常见重定向等。客户端浏览器必须采取更多操作来实现请求。

4XX类状态码信息表示：发生错误，客户端似乎有问题。例如：客户端请求不存在的页面

5XX类状态码信息表示：服务器遇到错误而不能完成该请求

* Cookie的一些属性：key value形式存储，有作用路径，失效时间，安全属性等

* keep-alive作用：1、减少tcp连接建立开销，2、tcp慢启动

* http代理服务器作用：代表客户端完成http事务的中间设备，有缓存服务器作用，反向代理，翻墙等

* http缓存服务器的作用：缓存常用文档副本的http设备，代理服务器的一种，1.减少冗余网络数据传输

  2.降低了原始服务器请求 3.降低距离时延

* 首部字段的意义：

```
//通用首部
Date:报文创建时间
Tranfer-Encoding:实体传输编码方式
Cache-control:缓存指示
//请求首部
host：请求服务器主机名和端口号（http1.1必传）
user-agent：请求客户端信息
accept-encoding:客户端能接受的编码方式
//响应首部
server：服务器端服务器、程序信息
accept-ranges:服务器可以接受的范围类型
//实体首部
content-type：实体类型 application/json
content-length:实体长度
content-encoding：实体编码方式 gzip
content-range:服务器可以接受的范围类型
```

#### 二、Httpclient的使用注意事项

```
必须要设置的参数：
连接超时时间
读取数据的超时时间
最大连接数
每个路由最大连接
```

#### 三、Jackson注意事项

常用注解和全局设置

```
–@JsonIgnore，
–@JsonProperty,  
–@JsonSerialize,
–@JsonDeserialize
–SerializationFeature.*
–DeserializationFeature.* 
```

