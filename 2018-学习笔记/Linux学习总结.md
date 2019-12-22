##### Linux学习总结

###### 文件操作：

sort: -k第几列， -n按数字排序， -r 倒叙排序

uniq:  uniq -c 

wc:  wc -l  

awk:

sed ‘/^$/d’  ： 删除空行



打包压缩：

tar -czvf  backup.tgz  bakup/  打包

tar -xzvf 压缩  tar -xzvf bakup.tgz  解压覆盖

tar -tzf  只查看不解压



shell运算符：

算术运算符：

vmstat 1  10

atnodes  在集群上运行制定命令

shell：

定义数组 ：array=(A B "C" D)

array[5]=F

array["test"]=G

${array["test"]}、${array[2]}

获取数组长度：

echo ${#array[@]}

获取数组元素的长度：

echo ${#array[2]}

###### shell传递参数：

$n  :  0为执行的文件名，1为执行脚本的第一个参数

$#  :  传递到脚本的参数个数

$*：显示所有参数

$@ : 显示所有参数，每个参数加引号

set -x 设置debug信息





最大文件

du -ah --max-depth=1

找最大文件

ls -lhsrS

查看出现最多的异常：



网络工具：

ping -c 4 www.baidu.com -q

hostname

nc与telent：

nc：网络发包工具

nc -t www.baidu.com 80 发tcp数据包

nc -u -v 127.0.0.1 8888发 udp数据包

nc -l -v 127.0.0.1 7070监听 端口

nc -t -v 127.0.0.1 7070

telent: 协议工具：tcp连接测试 telnet 10.86.42.63  6666

netstat命令：netstat -an

tcpdump抓包工具：

 ###### 远程命令：

ssh scp

scp 拷贝工具： scp 用户名@机器地址:/home/....文件  ./



###### 重要=进程与系统：

ps:    ps  -ef查看进程信息 ， ps  -Lf  25070:显示进程的线程信息

free查看内存使用情况 ：  free -m  以m为单位， free -h 自动选单位 

top 查看cpu使用率，内存信息，进程情况，cpu load等信息 ,  1显示多核信息

kill -9  pid 强制杀死进程



curl 命令请求

wget下载





copy到自己电脑：

python -m Simple



1、命令总结

2、效率提升



ctrl + r 历史命令模糊匹配

ctrl +a  移到行开头

ctrl +e  移到当前行的结尾

ctr+左右键

ctrl+w剪切前一个单词

ctrl+y 粘贴



vim:

:33 到33行

gg 行首 GG 行末

ctrl+d 翻半页  pageup down 翻页

/ 或? 查找  ， n N上下切换

yy复制一行，y+fanw p粘贴，dd 剪贴



shell流程控制：

while [[ $i -gt 5 ]]

((i--))

case $a in

   1) echo  '1'

   ;;

   2）echo '2'

   ; ;

   *)  echo '其他'

   ;;

esac



while :

do

​    read a

​    case $a in 

​     1|2|3|4)  echo "ddd"

​      ;;

​     *) echo "结束"

​           break (或continue)

​     ;;

​     esac

done   



shell提高：

cat  a.txt | xargs 将多行显示为1行

cat a.txt | xargs -n2  分组，每组2个

别名：

alias name = '命令'

alias name 显示

unalias name 取消别名

定时任务：

crontab

crontab -e 编辑该文件

rsync文件同步 



iostat  -d -k 1 10   #查看TPS和吞吐量信息

iostat  -d -x -k 1 10   #查看设备使用率（%util） 、响应时间(await)

iostat  -c  1  10   #查看cpu状态

vmstat 1 10  看r指标表示队列数



atnodes  在集群上运行指定命令

tonodes  上传本地文件到集群