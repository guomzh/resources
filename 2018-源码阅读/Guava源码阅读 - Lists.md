### Guava源码阅读 - Lists

#### 一、使用场景

reverse()方法的使用场景如下，返回[3, 2, 1]   （一个list）

```
Lists.reverse(Arrays.asList(1, 2, 3))
```

partition方法的使用场景如下,返回[[1, 2, 3], [4, 5]]   （一个list）

```
Lists.partition(Arrays.asList(1, 2, 3,4,5),3)
```

transform()方法使用场景如下，结果为[4, 2, 3]  （一个list）

```
List<String> list = Lists.newArrayList("我在阅读","源码","很开心");
System.out.println(Lists.transform(list, e->e.length()));//返回list中每个元素的长度的list
```

#### 二、源码分析（guava中好喜欢用静态内部类啊啊）

##### ①Reverse方法，真的要把一个list reverse一下吗？

reverse方法方法并不是真的把list reverse一下，返回的reverseList的底层原理是原List支持，只是取了反转后的位置元素而已，所以，对原先的List的改变会导致reverseList也跟着改变，其中传进来的List有四种类型情况：

```
public static <T> List<T> reverse(List<T> list) {
  if (list instanceof ImmutableList) {
    //如果是ImmutableList类型，则调用该类型list的reverse()方法获得reversed的list
    return ((ImmutableList<T>) list).reverse();
  } else if (list instanceof ReverseList) {
     //如果是ReverseList<T>类型，之间返回它的内部成员变量forwardList
     return ((ReverseList<T>) list).getForwardList();
  } else if (list instanceof RandomAccess) {
    //如果指定的list是RandomAccess类型的，返回的list也是random-access的，其中              RandomAccessReverseList<T>也是Lists中的静态内部类，继承自ReverseList<T>类
    return new RandomAccessReverseList<>(list);
  } else {
    //返回ReverseList<>类型，list的参数会被赋值给ReverseList的成员变量private final List<T> forwardList;
    return new ReverseList<>(list);
  }
}
```

ReverseList是Lists的静态内部类，它继承自AbstractList<T>抽象List接口，在ReverseList中有个与自己逆向的forwardList类型

```
private static class ReverseList<T> extends AbstractList<T> {
  private final List<T> forwardList;
  ...
  //由原先的index计算出逆转后对应的index, 如0123,第一个位置index为0，逆转后为(size-1)-index=(4-1)-0=3,所以逆转list对应index为3
   private int reverseIndex(int index) {
      int size = size();
      checkElementIndex(index, size);
      return (size - 1) - index;
    }
  //reversePosition和上面原理类似，主要用于出入删除元素位置
    private int reversePosition(int index) {
      int size = size();
      checkPositionIndex(index, size);
      return size - index;
    }
  //后面是一些AbstractList中方法的实现，包括迭代器等
  ...
}
```

##### ②partition方法

partition方法返回连续的subList即子list，每一个子list长度 一样，内部通过list.subList(start, end)实现但最后一个list可能长度不够小一点，输出的List是不可改变的，但是原list的最新状态映射。其中Partition<>是一个继承自AbstractList<List<T>>的静态内部类（**又是静态内部类！！！**），RandomAccessPartition在Partition<T>基础上多实现了RandomAccess接口而已

```
public static <T> List<List<T>> partition(List<T> list, int size) {
  checkNotNull(list);
  checkArgument(size > 0);
  return (list instanceof RandomAccess)
      ? new RandomAccessPartition<>(list, size)
      : new Partition<>(list, size);
}
```

```
private static class Partition<T> extends AbstractList<List<T>> {
  final List<T> list;//需要切分的list
  final int size;//每个字list的大小

  Partition(List<T> list, int size) {
    this.list = list;
    this.size = size;
  }
  
  @Override
  public List<T> get(int index) {
    checkElementIndex(index, size());//检查边界
    int start = index * size;//去第n个list，则开始下标为n*size
    int end = Math.min(start + size, list.size());//所取list的end下标
    return list.subList(start, end);//通过sublist方法取list的子list
  }

  @Override
  public int size() {
    //真个list分为几个子list，向上取整
    return IntMath.divide(list.size(), size, RoundingMode.CEILING);
  }
   ...
}
```

##### ③transform，将集合里每个对象转换完然后再填入一个新的list吗?

TransformingSequentialList<F, T>是一个继承自AbstractSequentialList<T>并实现了序列化接口的**静态内部类**，它有两个成员**常量**，一个是待转化的原始list，一个是Function<? super F, ? extends T> function函数式接口，作用是通过迭代，运用function中的apply函数把list中的每个元素转为需要转化成的元素，因此只是改了迭代方法，并没有转化后填入一个新的list。需要重写函数式接口Function中的apply方法，在此学习借鉴了Function接口的用法精髓

```
public static <F, T> List<T> transform(
    List<F> fromList, Function<? super F, ? extends T> function) {
  return (fromList instanceof RandomAccess)
      ? new TransformingRandomAccessList<>(fromList, function)
      : new TransformingSequentialList<>(fromList, function);
}
```

```
private static class TransformingSequentialList<F, T> extends AbstractSequentialList<T>
    implements Serializable {
  final List<F> fromList;//待转化的List
  final Function<? super F, ? extends T> function;//函数式接口Function

  TransformingSequentialList(List<F> fromList, Function<? super F, ? extends T> function) {
    this.fromList = checkNotNull(fromList);//检查null
    this.function = checkNotNull(function);//检查null
  }

  /**
   * The default implementation inherited is based on iteration and removal of each element which
   * can be overkill. That's why we forward this call directly to the backing list.
   */
  @Override
  public void clear() {
    fromList.clear();
  }

  @Override
  public int size() {
    return fromList.size();//转化后的list的size和原理的List一样
  }

  @Override
  public ListIterator<T> listIterator(final int index) {
    //在迭代方法中通过apply方法转化元素
    return new TransformedListIterator<F, T>(fromList.listIterator(index)) {
      @Override
      T transform(F from) {
        return function.apply(from);
      }
    };
  }

  @Override
  public boolean removeIf(Predicate<? super T> filter) {
    checkNotNull(filter);
    //评估条件是否成立，成立则删掉该元素，函数式接口Predicate的运用
    return fromList.removeIf(element -> filter.test(function.apply(element)));
  }

  private static final long serialVersionUID = 0;
}
```

#####  拓展：有时间再研究下各种迭代器的源码原理

