### Guava源码阅读 -   Joiner

#### 一、概览

* Joiner的主要使用场景为把一个数组或集合中的数据用特定的分隔符连接起来，典型 的使用场景如：

```
 public static String joinByGuava(List stringList, String delimiter) {
      return    Joiner
                 .on(delimiter)
                 .skipNulls()
                 .join(stringList);
  }
```

他会把stringList中的null值排除，然后用分割符连接起来

* MapJoiner是它的一个静态内部类，它主要是根据keyValueSeparator分隔符把字符串转为Map

```
private MapJoiner(Joiner joiner, String keyValueSeparator) {
  this.joiner = joiner; // only "this" is ever passed, so don't checkNotNull
  this.keyValueSeparator = checkNotNull(keyValueSeparator);
}
```

#### 二、appendTo()方法

Joiner类中通过众多重载的appendTo方法把迭代器中的各个值append起来，最后可以通过toString（）方法返回需要的字符串，下面是其中的一段源码中对appendTo的实现，Appendable是jdk中的一个接口

```
@CanIgnoreReturnValue
public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts) throws IOException {
  //检查是否传进来的appendable为null
  checkNotNull(appendable);
  if (parts.hasNext()) {
    //添加迭代器的下一个元素
    appendable.append(toString(parts.next()));
    while (parts.hasNext()) {
    
    //添加分隔符
      appendable.append(separator);
      appendable.append(toString(parts.next()));
    }
  }
  return appendable;
}
```

```
//重载上面的appendTo方法
@CanIgnoreReturnValue
public <A extends Appendable> A appendTo(A appendable, Iterable<?> parts) throws IOException {
  return appendTo(appendable, parts.iterator());
}


@CanIgnoreReturnValue
public final <A extends Appendable> A appendTo(A appendable, Object[] parts) throws IOException {
  //把需要连接的数组转化为ArrayList(实现了iterable接口)，再重载上面的方法
  return appendTo(appendable, Arrays.asList(parts));
}

//第一二个参数为对象，打三个为参数列表，再重载上面的appendTo（）方法
@CanIgnoreReturnValue
public final <A extends Appendable> A appendTo(
    A appendable, @Nullable Object first, @Nullable Object second, Object... rest)
    throws IOException {
  return appendTo(appendable, iterable(first, second, rest));
}

//StringBuilder类型，和上面方法类似
@CanIgnoreReturnValue
public final StringBuilder appendTo(StringBuilder builder, Iterable<?> parts) {
  return appendTo(builder, parts.iterator());
}


@CanIgnoreReturnValue
public final StringBuilder appendTo(StringBuilder builder, Iterator<?> parts) {
  try {
    appendTo((Appendable) builder, parts);
  } catch (IOException impossible) {
    throw new AssertionError(impossible);
  }
  return builder;
}


@CanIgnoreReturnValue
public final StringBuilder appendTo(StringBuilder builder, Object[] parts) {
  return appendTo(builder, Arrays.asList(parts));
}


@CanIgnoreReturnValue
public final StringBuilder appendTo(
    StringBuilder builder, @Nullable Object first, @Nullable Object second, Object... rest) {
  return appendTo(builder, iterable(first, second, rest));
}
```

由上面可以看出，appendTo方法基本实现方式一致，通过迭代器,跌倒添加到StringBuilder中，最后返回连接的字符串。