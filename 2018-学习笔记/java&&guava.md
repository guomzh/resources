今天的任务主要是深入巩固java&&guava基础内容。以下是对今天学习内容的一些总结和思考。

##### 一、关于日志

程序中出现System.out.println()等语句是及其不规范的。所以之前提交的作业也是不规范的，应该使用slf4j规范, 然后使用logback、log4j等实现打印日志信息，同时为了避免日志依赖冲突还应使用scope为runtime的形式。

##### 二、关于在使用Thread时遇到的一些问题和知识总结：

* 线程 - daemon，使线程随着主线程的退出而退出。

* 线程在其他线程完成后退出：  通过thread.join实现。

* 线程控制方面：  thread.interrupted会清除标志位，而thread.isInterrupted则只判断。

* 常用两种类型线程池：FixedThreadPool和CachedThreadPool。并可通过自定义ThreadFactory方法来创建线程，使用场景通常是为线程起名，是否设置为后台

* 让线程池随着主线程的退出而退出方法：可以通过定义ThreadFactory，在其中设置线程thread.setDaemon(true)来达到目的，如：

```
 Executors.newFixedThreadPool(5,r->{
      Thread thead = new Thread(r, "name: "+ threadCount.getAndIncrement());
      thread.setDaemon(true);    
});
```

##### 三、对ExecutorException的疑惑

 Future的get方法（一般使用带有时间参数的,超时抛出异常）会一直等待子任务完成。

其中ExecutorService可能会抛出InterruptedException或者ExecutionException。然而对ExecutionException异常调用getMessage()方法可能不会打印最起始的异常，只打印包装的异常，需要不断地调用异常的getCause()方法直到为null，再调用getMessage()才能获得异常的真正原因

#####  四、关于volatile变量和锁的思考

我理解的volatile： 保证变量对线程的最新可见性，然而不是线程安全的，使用时该变量不应该依赖前值，需谨慎使用。通过对Java内存模型的探索，加深了理解。



##### 明天的计划

* 继续深入学习Java和guava常用的集合类型

* 深入学习lambda表达式
* 优先完成作业
* 利用这几天已学知识对前面的作业进行重新改造
* 把作业工程改为一个比较符合maven规范的工程，熟悉Maven规范

