##### 题目1 文件检测和目录创建
输入：路径
输出：如果某路径不存在，则将其创建为目录；否则显示其存在，并输出其是目录还是文件
##### 题目2 文件行统计
(1) 传递两个文本文件路径给脚本
(2) 输出两个文件中空行数较多的文件名字及其空行的个数
(3) 输出两个文件中总行数较多的文件名字及其总行数
##### 题目3 重复文件查找
编写脚本，输入一个或者多个目录，查找和输出这些目录中内容相同的文件，将相同的文件分组输出。
注: 判断文件内容是否相同，可以使用文件md5值。
##### 题目4 curl练习
###### 服务地址
l-marmotui1.wap.beta.cn0.qunar.com:1853
###### 登录
> 接口
|接口|解释|请求|
|-|-|-|
|/graduate/api/login|登录接口|POST，表单提交|

> 参数
|名字|类型|解释|
|-|-|-|
|username|String|用户名|
|password|String|密码明文|

> 返回
|名字|类型|解释|
|-|-|-|
|code|int|状态码|
|errMsg|String|错误说明|

> 说明
用户名:graduate
密码:graduate_password
返回值格式为json，code为0时，表示访问成功
###### 提交
> 接口
|接口|解释|请求|
|-|-|-|
|/graduate/api/submit|提交信息接口|POST，json数据|

> 参数
|名字|类型|解释|
|-|-|-|
|name|String|姓名|
|mobile|String|手机号|
|people_num|int|人数|

> 返回
|名字|类型|解释|
|-|-|-|
|code|int|状态码|
|errMsg|String|错误说明|

> 说明
返回值格式为json，code为0时，表示访问成功
##### 题目5 AWK练习
从下面日志获取flightnum值和main flightnum值（提示gawk gensub函数的使用）
日志：
flightnum is CA1230，main flightnumMU1230
flightnum is MU1111，main flightnumCA1111
输出：
CA1230 MU1230
MU1111 CA1111
