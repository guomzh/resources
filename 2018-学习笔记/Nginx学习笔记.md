###   Nginx学习笔记

定义：高性能的HTTP server、反向代理服务器

#### 正向代理和反向代理

正向代理：local  ->  Proxy  -> www.google.com   通过代理隐藏真正访问用户

反向代理：client  ->   www.qunar.com  ->   ( real server1  、 real  server2 .... ) ,  由nginx做负载均衡 ， 用户并不知道有多少台服务器

#### 代码结构：

​     worker代码：

* 核心模块
* 功能模块

 主要模块：   handlers  、 Filters   、 Upstreams 、 load balances

#### Nginx基本控制

yum install q-nginx

--安装路径： /home/q/nginx

--配置文件路径： /home/q/nginx/conf

--静态内容放置目录： /home/q/nginx/html

--日志路径： /home/q/nginx/logs

启动： sudo /home/q/nginx/sbin/nginx  -c  /home/q/nginx/conf/nginx.conf

运行时nginx的控制命令： 通过 -s  后面加参数：  -stop退出 、 -quit优雅退出 、 -reload重加载  、 reopen 重新

-t 测试配置文件

打开日志文件

#### Nginx配置文件

nginx.conf基本配置文件、mime.types文件扩展列表文件、fastcgi.conf

指令：user www www;

指令快 ：

```
http{server{}}
```

文件包含：

include mime.types;

配置文件结构：

Main  context： 包含一些系统级设置，运行用户等

Even context ： 包含对连接做处理的配置

Http context  ： 包含 upsteam ,  server ,  location等

**server配置**：

**listen**： 设置web服务监听套接字的ip地址/端口

listen  123.59.189.164:80;  

listen  80;

**server_name** ：设置虚拟主机的名称

server_name example.org;

server_name *.example.org;

gzip：压缩

#### Location : 对具体的URL配置 

Syntax : 

location [ = | ~ |~* |^~ ] uri {...}

location  @name

Default： —

Context：server , location

**例子：**

```
//严格匹配
Server{
    listen 80;
    server_name _;
    location = /foo{·
        //测试一下
        echo "$uri";
    }
}
//   只有http://localhost/foo匹配
```

```
//无修饰符
Server{
    listen 80;
    server_name _;
    location /foo{
        echo "$uri";
    }
}
//   http://localhost/foo匹配
//   http://localhost/foo/匹配
//   http://localhost/foobar匹配
```

```
//正则匹配，运用非常多^以什么开头，$以什么结尾
Server{
    listen 80;
    server_name _;
    location ~ ^/foo${
        echo "$uri";
    }
}
// http://localhost/foo
// http://localhost/foobar 不匹配
```

```
//跟上面~一样，只是不区分大小写
Server{
    listen 80;
    server_name _;
    location ~* ^/foo${
        echo "$uri";
    }
}
```

```
^~修饰符，如果最大匹配前缀以^~开始，则停止正则匹配
@修饰符，只能内部的请求访问 ，客户访问不到
优先级：带“=”location > 没有修饰符的location > 带^~的locatioin > 带 ~和~*的location
```

**rewrite** :  替换重定向，  如 rewrite  ^  http://m2.qunar.com  即匹配上了用后面的替换

如 rewrite ^ http://m2.qunar.com$uri  把参数保留

如 rewrite ^ http://m2.qunar.com$uri?; 把后面参数丢掉

if条件判断  if(condition){rewrite ^ http://.....},     Context是 server，location

**return**: 停止请求，返回响应的code 如 ：  return 301  http://m2.qunar.com

**set**:   如 set  $variable  value;  Context是：server,location , if

**proxy**： 

```
proxy_pass http://flight_inter_seo;
proxy_set_header  Host   $host; 修改转到后台的域名
proxy_set_header  X-Real-Scheme  $scheme;  区分http和https
proxy_set_header  X-Real-IP  $remote_addr;
proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;看到代理路径的所有地址
```

```
修改用户请求的目录：
location /name/{
    proxy_pass http://http_backend;
}
```

proxy_next_upstream: 错误转发

```
proxy_intercept_errors on;
error_page 404 /qunar_404.html;
error_page 500 501 /qunar_500.html;
proxy_next_upstream error timeout invalid_header http_500 http_502;出错后转到正常的服务器
```

#### upstream:转发到具体服务器

```
upstream   flag_003{
     server 192.168.35.23:808
     server 192.168.35.24:8080;
     hash $remote_addr;
     hash_again 10;
}
```

负载均衡策略：

* round-robin:轮询
* hash 分一致性hash 和ip hash等
* least connection 最小连接数，根据服务器处理能力分，使用很少
* weight 按权重分，常使用

#### ngx-limit-req2:

--限制用户访问的频率 ，或者禁止用户的访问。

--基于ip限制

**--备忘**

线上ngin配置查看：

http://ops.corp.qunar.com/ops/nginxdb/#/

opsrt:

http://ops.corp.qunar.com选择rt工单

beta和dev环境：

http://manageng.beta.qunar.com