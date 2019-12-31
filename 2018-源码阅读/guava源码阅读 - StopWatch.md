### guava源码阅读 - Stopwatch

#### 一、作用

StopWatch是guava中一个以纳秒为时间单位测量过去的时间的对象。

用法为 ：

```
        Stopwatch stopwatch = Stopwatch.createStarted();
        Thread.sleep(1000);
        stopwatch.stop(); // optional
        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("time: {}",millis); // "time: 1001"
```

#### 二、源码分析

##### 成员变量：

```
private final Ticker ticker;//计时器
private boolean isRunning;//是否正在运行
private long elapsedNanos;//过去了多少时间，Nanos为单位
private long startTick;//开始的计时时间
```

##### 工厂方法：

这个工厂方法最常用，new Stopwatch()的默认构造函数会吧ticker设置为默认的Ticker.systemTicke(),同时调用start（）方法开始这个计时器，

```
public static Stopwatch createStarted() {
  return new Stopwatch().start();
}
```

```
public Stopwatch start() {
  checkState(!isRunning, "This stopwatch is already running.");//检查状态
  isRunning = true;//设置为正在运行
  startTick = ticker.read();//读取开始计时的时间
  return this;
}
```

##### 停止计时：

这个方法会返回一个固定的从开始到这个点的持续时间（elapseNanos）的Stopwatch

```
public Stopwatch stop() {
  long tick = ticker.read();
  checkState(isRunning, "This stopwatch is already stopped.");
  isRunning = false;
  elapsedNanos += tick - startTick;//设置过去的时间
  return this;
}
```

##### 重置计时器：

```
public Stopwatch reset() {
  elapsedNanos = 0;//把elapseNanos过去的时间置为0
  isRunning = false;//设置运行状态为否
  return this;//返回这个计时器
}
```

##### 计算过去的时间方法：

使用三元运算符判断状态（值得学习这种写法），先判断状态是否在运行，在运行则时间为上次过去的时间加上这次读取过去的时间，否则直接返回elapseNanos过去 的时间

```
 public long elapsed(TimeUnit desiredUnit) {
    return desiredUnit.convert(elapsedNanos(), NANOSECONDS);
  }
```

返回Duration的写法，这样不会因为四舍五入等丢失任何精度

```

public Duration elapsed() {
  return Duration.ofNanos(elapsedNanos());
}
```

##### toString()方法：

返回过去的时间的一个字符串表示

```
public String toString() {
  long nanos = elapsedNanos();
  TimeUnit unit = chooseUnit(nanos); //根据时间长度选择合适的时间单位
  //先把选择的单位转化为多少NANOSECONDS，返回多少选择的单位时间
  double value = (double) nanos / NANOSECONDS.convert(1, unit);
  // Too bad this functionality is not exposed as a regular method call
  //abbreviate转化时间单位为缩写，Platform.formatCompact4Digits(value)把一个double类型的值转化为4小数点的字符串
  return Platform.formatCompact4Digits(value) + " " + abbreviate(unit);
}
```

##### Platform.formatCompact4Digits(value)  ：  将一个double类型转化为保留4位小数点的字符串

