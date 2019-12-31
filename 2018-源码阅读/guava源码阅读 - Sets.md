### guava源码阅读 - Sets

#### 一、Union方法实现源码分析

union返回的是两个set的并集SetView（实现了AbstractSet<>接口）的视图，因此它的底层还是原来的那两个set，如果对支持set的改变，会导致这个并集set的改变。下面是分析过程：

```
public static <E> SetView<E> union(final Set<? extends E> set1, final Set<? extends E> set2) {
  checkNotNull(set1, "set1");
  checkNotNull(set2, "set2");
  
  return new SetView<E>() {
    //重写size()方法
    @Override
    public int size() {
      int size = set1.size();
      for (E e : set2) {
        if (!set1.contains(e)) {
          size++;
        }
      }
      return size;
    }
    ...
    //迭代器方法，
    @Override
    public UnmodifiableIterator<E> iterator() {
      return new AbstractIterator<E>() {
        final Iterator<? extends E> itr1 = set1.iterator();
        final Iterator<? extends E> itr2 = set2.iterator();

        @Override
        protected E computeNext() {
          if (itr1.hasNext()) {
            return itr1.next();
          }
          while (itr2.hasNext()) {
            E e = itr2.next();
            if (!set1.contains(e)) {
              return e;
            }
          }
          //返回null，同时把状态设置为State.DONE,这是AbstractSet中的枚举类型
          return endOfData();
        }
      };
    }
   //返回两个set流的合并,用Stream.concat()方法
    @Override
    public Stream<E> stream() {
      return Stream.concat(set1.stream(), set2.stream().filter(e -> !set1.contains(e)));
    }
    //返回并行流
    @Override
    public Stream<E> parallelStream() {
      return stream().parallel();
    }
    //把并集set元素拷贝到一个传进来的set
    @Override
    public <S extends Set<E>> S copyInto(S set) {
      set.addAll(set1);
      set.addAll(set2);
      return set;
    }
    //拷贝到一个ImmutableSet,构造器模式了解一下
    @Override
    public ImmutableSet<E> immutableCopy() {
      return new ImmutableSet.Builder<E>().addAll(set1).addAll(set2).build();
    }
  };
}
```

#### 二、intersection方法实现源码分析

instersection方法的实现和union方法差不多，它的iterator是一个复写了remove方法的UnmodifiableIterator<E>，强制如果删除里面的元素则抛出异常。

```
@GwtCompatible
public abstract class UnmodifiableIterator<E> implements Iterator<E> {
  /** Constructor for use by subclasses. */
  protected UnmodifiableIterator() {}

  @Deprecated
  @Override
  public final void remove() {
    throw new UnsupportedOperationException();
  }
}
```

```
public static <E> SetView<E> intersection(final Set<E> set1, final Set<?> set2) {
  checkNotNull(set1, "set1");
  checkNotNull(set2, "set2");

  return new SetView<E>() {
    @Override
    public UnmodifiableIterator<E> iterator() {
      return new AbstractIterator<E>() {
        final Iterator<E> itr = set1.iterator();
        //重写了computeNext方法
        @Override
        protected E computeNext() {
          while (itr.hasNext()) {
            E e = itr.next();
            if (set2.contains(e)) {
              return e;
            }
          }
          return endOfData();
        }
      };
    }
    //通过Collections.disjoint（）方法判断没有交集
    @Override
    public boolean isEmpty() {
      return Collections.disjoint(set1, set2);
    }
  };
}
```

