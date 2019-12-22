### CompletableFuture特性 - 异步编程

#### 一、介绍和作用

CompletableFuture大约有五十种方法，其中它们中的一些非常有意思而且不好理解。

它内置了的lambda表达式，CompletableFuture有两个主要的方面优于原先jdk中的Future – **异步回调/转换**，这能使得从任何时刻的任何线程都可以设置CompletableFuture的值。

```
public CompletableFuture<String> ask() {
    final CompletableFuture<String> future = new CompletableFuture<>();
    //...
    return future;
}
```

这个future和Callable没有任何联系，没有线程池也不是异步工作。如果现在客户端代码调用ask().get()它将永远阻塞。

`future.complete(1)`;

ask().get()将得到字符串的结果，同时完成回调以后将会立即生效。CompletableFuture.complete()只能调用一次，后续调用将被忽略。

```
/**
 * 使用一个预定义的结果创建一个完成的CompletableFuture,通常我们会在计算的开始阶段使用它。
 */
public static void completedFutureExample() {
    CompletableFuture cf = CompletableFuture.completedFuture("message");
    System.out.println(cf.isDone());
    System.out.println(cf.getNow(null));
}
```

#### 二、创造和获取CompletableFuture，使用工厂方法

``````
      //CompletableFuture用于java中的异步编程
      public static CompletableFuture<Void> runAsync(Runnable runnable)
      public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
      public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
      public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
``````

```
//例一：有返回值
public static void supplyAsync() throws Exception {
    CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        System.out.println("run end ...");
        return System.currentTimeMillis();
    });
    long time = future.get();
    System.out.println("time = "+time);
}
```



**没有指定Executor的方法会使用ForkJoinPool.commonPool() 作为它的线程池执行异步代码，会成为Daemon线程执行，为了简化监视、调试和跟踪。**
 **如果指定线程池，则使用指定的线程池运行。**

#### 三、守护线程与用户线程的区别

用户线程：就是我们平时创建的普通线程.

守护线程：主要是用来服务用户线程.

**当线程只剩下守护线程的时候，JVM就会退出.但是如果还有其他的任意一个用户线程还在，JVM就不会退出**

举个栗子：

```
public class DaemonRunnable implements Runnable {
    @Override
    public void run() {

            for (int i = 1; i <= 1000; i++) {
                try {
                    System.out.println("我是守护线程： "+ i);
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }

    public static void main(String[] args) {
        Thread daemonThread = new Thread(new DaemonRunnable());
        // 设置为守护进程
        daemonThread.setDaemon(true);
        daemonThread.start();
        Scanner scanner = new Scanner(System.in);
        //阻塞
        scanner.nextLine();
        System.out.println("唯一的用户线程main结束，jvm退出 , 守护线程不得不停止了");
    }
}
```

#### 四、CompletableFuture(thenApply)，注册异步回调

如果某些转换非常耗时，你可以提供你自己的Executor来异步地运行他们。

```
<U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn);
<U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn);
<U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor);
CompletableFuture<String> f1 = //...
CompletableFuture<Integer> f2 = f1.thenApply(Integer::parseInt);
CompletableFuture<Double> f3 = f2.thenApply(r -> r * r * Math.PI);
```

#### 五、运行完成的代码（thenAccept/thenRun）

```
CompletableFuture<Void> thenAccept(Consumer<? super T> block);
CompletableFuture<Void> thenRun(Runnable action);
```

thenAccept或者thenAcceptAsync方法得到一个没有返回值类型的CompletableFuture，它们像一个事件侦听器/处理程序，如以下代码，日志会立即打印出"Continuing"。

```
completableFuture.thenAcceptAsync(result -> log.debug("Result: {}", result), executor);
logger.debug("Continuing");
```

#### 六、多种结合的CompletableFuture

我们现在知道如何等待两个future来完成（使用thenCombine()）并第一个完成(applyToEither())。但它可以扩展到任意数量的futures吗？的确，使用static辅助方法：

`static` `CompletableFuture<Void< allOf(CompletableFuture<?<... cfs)`

工具类：

```
类似Guava中Futures.successfulAsList(ListenableFuture<? extends V>... futures)，
实现CompletableFutures.succeessfulAsList(CompletableFuture<? extends V>... futures)
```

```
    public static <V> CompletableFuture<List<V>> succeessfulAsList(CompletableFuture<? extends V>... futures) {

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures);
        //题目要求返回的结果
        CompletableFuture<List<V>> resultFuture = new CompletableFuture<>();
        //所有任务完成时
        allFutures.whenComplete((o, throwable) -> {
            if (throwable != null) {
                LOGGER.warn("出现异常当执行： {}", throwable.getMessage());
            } else {
                List<V> results = new ArrayList<>(futures.length);
                for (CompletableFuture<? extends V> future : futures) {
                    try {
                        V result = future.get();
                        results.add(result);
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.warn("任务执行异常： {}", e.getMessage());
                        results.add(null);
                    }
                }
                resultFuture.complete(results);
            }
        });
        return resultFuture;
    }
}
```

测试该工具类：

```
 ExecutorService executors = Executors.newFixedThreadPool(3, new ThreadFactory() {
            int count = 1;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "My thread name is t_" + count++);
            }
        });
        List<CompletableFuture<String>> futures = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(3000L));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "我的线程名字叫： " + Thread.currentThread().getName();
            }, executors);
            futures.add(completableFuture);
        }
        CompletableFuture<List<String>> future = CompletableFutures.succeessfulAsList(futures.toArray(new CompletableFuture[3]));
        System.out.println("所有future都完成了吗 ： " + future.isDone());
        try {
            Thread.sleep(5000L);
            System.out.println("我睡了5秒，再看看所有线程完成了没！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("所有future都完成了吗 ： " + future.isDone());


```