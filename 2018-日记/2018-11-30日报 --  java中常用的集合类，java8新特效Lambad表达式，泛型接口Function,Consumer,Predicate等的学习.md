2018-11-30日报 --  java中常用的集合类，java8新特效Lambad表达式，泛型接口Function,Consumer,Predicate等的学习



今天学习的内容主要为java中常用的集合类，java8新特效Lambad表达式，泛型接口Function,Consumer,Predicate等的学习。主要遇到的学习难点和收获思考如下：

##### 一、泛型擦除机制和泛型方法的运用

* java中的泛型只存在于编译期，当编译完成后泛型擦除，为了验证此机制我发现可以通过反射来给一个List<Integer>  list集合中添加一个String类型的对象。

* 之前在写代码时很少使用泛型方法，今天熟悉了泛型方法的一些特性和使用场景，主要是避免写重复的方法，如

  ```
   public <T> genericMethod(Class<T> t) throws InstantiationException,
        IllegalAccessExceotion{  }
  ```

##### 二、集合类的线程安全问题

当通过多个线程并非地向一个ArrayList中添加大量元素的时候，会发现最终得到的结果没有达到预期元素个数。因此需使用线程安全集合，如，

List中的CopyOnWriteArrayList，适合写少读多的场景；

关于Map中的 ConcurrentHashMap ， 通过查阅相关资料和源码总结如下：

在java7和之前使用Segment来实现小锁粒度，通过把HashMap分割成若干个Segment（默认为16），每个Segment继承自ReentrantLock，在put的时候需要锁住Segment，get时候不加锁，使用volatile来保证可见性，当长度过长碰撞会很频繁，链表的增改删查操作都会消耗很长的时间，影响性能。但有同事提出java8中不是使用ReentrantLock，今天回去继续研究一下。

##### 三、在java包中新增的函数式接口  --  函数式编程

@FunctionalInterface指一个接口只有一个非default,非static的接口

如Function接口，Predicate接口

Cunsumer接口、Supplier接口以及如何扩展？

考虑如下几个因素：

参数个数、泛型关系限制、基本类型、返回值类型。。。。。

lambda表达式和java8中Stream结合函数式编程的应用，这部分还不是特别深入，周末继续研究。