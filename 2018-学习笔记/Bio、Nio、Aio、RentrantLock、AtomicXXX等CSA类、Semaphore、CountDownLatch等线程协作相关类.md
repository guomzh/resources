#### Bio、Nio、Aio、RentrantLock、AtomicXXX等CSA类、Semaphore、CountDownLatch等线程协作相关类

##### 一、io

Bio通过accept（）阻塞

多路复用io(非阻塞)：轮询,具体流程

系统调用 -> selector处理 ->  轮询  ->  selector统一处理数据，返回给应用 -> 循环此过程

异步io  (Async IO)  从jdk1.7开始引入，具体过程：

应用发出系统调用后立即返回  ->  系统等待数据，得到数据后处理，复制返回数据给用户 ->  应用回调 

##### 二、Lock & ReentrantLock

fail  :  一个后来的线程是否可以先获得锁

synchronized 非公平锁， 经优化后不一定重 ， 更简单和易于维护

ReentrantLock  :  可重入的 ， 公平锁， 使用tryLock来给定获取锁的时间限制

ReadWriteLock  :  有一个默认实现，同样是Reentrant, 同样是可重入， 公平锁，  适用于写少加锁，读多可并发进行不加锁

##### 三、CAS相关类型

CAS定义  :  Compare And  Set/Swap   只会设置新值当旧值和期待的一样

基本类型： AtomicInteger 、 AtomicLong（更优化的LongAdder） 、 AtomicBoolean

AtomicReference ： 对自定义类进行原子类操作

AtomicXXXFieldUpdater: 使用反射进行原子类操作

##### 四、多线程协调

* Semaphore  -  permits 个数问题

​         可以通过Semaphore的tryAcquire(timeout, TimeUnit)方法类似获得锁一样获得 permits， 同样，需在finally方法中通过semaphore.release()来释放permit

​         通过release()方法的多次调用可以动态修改permit个数

* CountDownLatch  :  让某个线程等待所有其他线程完成，再执行汇总操作

​        使用方法，通过在每个子任务的finally方法中调用 countDownLatch.countDown()方法，最后调用countDownLatch.await()会等待所有任务完成

关键语法：

```
CountDownLatch countDownLatch = new CountDownLatch (jobCount);
countDownLatch.countDown();
//汇总
countDownLatch.await();
```

* CyclicBarrier :   所有线程都等待其他线程完成，再开始第二阶段的任务，可以理解为任务分层

​       使用方法， 通过在子任务中调用cyclicBarrier.await()方法等待其他任务，在开始第二阶段的任务

关键语法：

```
cyclicBarrier.await();
//开始第二阶段任务，有点像一轮又一轮赛马，只有第一轮所有马都到终点后才能开始第二轮赛马
```

allof方法解决第3题？

completableFuture使用场景一连串转换操作，其中没有if else逻辑操作？

##### 五、ListenableFuture, Futures等Guava中扩展的接口及工具类的使用



##### 六、CompletableFuture的使用

