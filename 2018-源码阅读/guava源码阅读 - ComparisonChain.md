#### guava源码阅读 - ComparisonChain

使用ComparisonChain可以优雅地实现多个比较条件不用写if else，链式api设计思想

通用的使用方法：

```
 public int compareTo(Foo that) {
   return ComparisonChain.start()
       .compare(this.aString, that.aString)
       .compare(this.anInt, that.anInt)
       .compare(this.anEnum, that.anEnum, Ordering.natural().nullsLast())
       .result();
 }
```

在该ComparisonChain抽象类中，定义了三个它本身自己的实例，ACTIVE 、LESS 、 GREATER

```
private static final ComparisonChain ACTIVE =
    new ComparisonChain() {  
       @overite
       一些这个类本身定义的抽象比较方法
    }
```

```
private static final ComparisonChain LESS = new InactiveComparisonChain(-1);

private static final ComparisonChain GREATER = new InactiveComparisonChain(1);
```

重点在于：

```
ComparisonChain classify(int result) {
  return (result < 0) ? LESS : (result > 0) ? GREATER : ACTIVE;
}
```

比较完后会对结果（-1,0,1）做分类：

如果是0，说明这个  比较条件相等，则ACTIVE比较器链又返回ACTIVE自己，继续比较下一个条件。

如果是-1，说明小于比较对象，这时比较结果已经出来了，就是-1，则再返回LESS这个比较器链对象，这个对象再调用compare方法，依然返回LESS，比较结果还是-1。这样就实现了链式比较器，好处是不断用.compare()方法去比较就可以了，不用写if else，如果一个条件比较相等再比较另一个条件。

如果是1，逻辑同-1一样，不断返回GREATER比较器本身，说明结果为大于。

```
private static final class InactiveComparisonChain extends ComparisonChain {
  final int result;

  InactiveComparisonChain(int result) {
    this.result = result;
  }

  @Override
  public ComparisonChain compare(@Nullable Comparable left, @Nullable Comparable right)   {
    return this;
  }
   。。。此次省略一些比较方法重写
   //本方法就是，如果是LESS或者GREATER比较器，则表示已经比出大小了，返回result（-1或者1）就可以了
  @Override
  public int result() {
    return result;
  }
}
```

总结：链式api设计 很巧妙，比较结果是0，则返回比较器链对象本身继续比较，如果是-1或则1，则返回一个不活跃的链式比较器链InactiveComparisonChain，很过就会返回result终结比较。