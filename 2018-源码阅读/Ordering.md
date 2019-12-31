#### Ordering

返回根据这个顺序给出迭代，从最大到最小的k个最大的元素。它的reverse()方法返回一个ReverseOrdering,这个类继承自Ordering，但重写了max,min等比较方法，所以它返回一个ReverseOrdering的最小的K个元素即k个最大元素

```
<E extends T> List<E> greatestOf(Iterable<E> iterable, int k)
```

```
public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k) {
  // TODO(kevinb): see if delegation is hurting performance noticeably
  // TODO(kevinb): if we change this implementation, add full unit tests.
  return reverse().leastOf(iterable, k);
}
```

返回从给定的迭代器按照这个顺序，从最大到最小k个最大的元素。实现过程同上。


```
<E extends T> List<E> greatestOf(Iterator<E> iterator, int k)
```

```
public <E extends T> List<E> greatestOf(Iterator<E> iterator, int k) {
  return reverse().leastOf(iterator, k);
}
```

下面来分析下它怎么求最小的k个元素，如果是一个可迭代集合即iterable instanceof Collection，则利用Arrays.sort(array, this);方法来排序，最后把排序后前k个元素通过Arrays.copyOf(array, k)方法复制出来即可。

```
public <E extends T> List<E> leastOf(Iterable<E> iterable, int k) {
  if (iterable instanceof Collection) {
    Collection<E> collection = (Collection<E>) iterable;
    if (collection.size() <= 2L * k) {
      // In this case, just dumping the collection to an array and sorting is
      // faster than using the implementation for Iterator, which is
      // specialized for k much smaller than n.

      @SuppressWarnings("unchecked") // c only contains E's and doesn't escape
      //把集合转化为数组后通过Arrays.sort（）排序
      E[] array = (E[]) collection.toArray();
      Arrays.sort(array, this);
      if (array.length > k) {
        array = Arrays.copyOf(array, k);
      }
      return Collections.unmodifiableList(Arrays.asList(array));
    }
  }
  return leastOf(iterable.iterator(), k);
}
```

如果是对一个iterator迭代器排序，发现一个guava中好用的排序方法:  TopKSelector<E> selector = TopKSelector.least(k, this); selector.offerAll(iterator); selector.topK(); 这里是如果集合大小大于Integer.MAX_VALUE / 2，则采用    Collections.sort(list, this);方法排序，如果 小于，则用谷歌guava的TopKSelector排序。

```
public <E extends T> List<E> leastOf(Iterator<E> iterator, int k) {
  checkNotNull(iterator);
  checkNonnegative(k, "k");

  if (k == 0 || !iterator.hasNext()) {
    return Collections.emptyList();
  } else if (k >= Integer.MAX_VALUE / 2) {
    // k is really large; just do a straightforward sorted-copy-and-sublist
    ArrayList<E> list = Lists.newArrayList(iterator);
    Collections.sort(list, this);
    if (list.size() > k) {
      list.subList(k, list.size()).clear();
    }
    list.trimToSize();
    //返回一个不可变的集合
    return Collections.unmodifiableList(list);
  } else {
    //guava中取topk元素的方法
    TopKSelector<E> selector = TopKSelector.least(k, this);
    selector.offerAll(iterator);
    return selector.topK();
  }
}
```

