#### Guava源码阅读 - ImmutableList

##### 1、阅读前的了解：当不希望修改一个集合类，或者想做一个常量集合类的时候，使用immutable集合类就是一个最佳的编程实践。包括list,set,map等

##### 2、Immutable集合使用方法：

- ImmutableList.copyOf(list)

- ImmutableSet.of("a", "b", "c")或者ImmutableMap.of("a", 1, "b", 2)

- 使用Builder类

  ```
  ImmutableSet.<String>builder()
          .add(new String("hh"))
          .add(new String("gg"))
          .build();
  ```

3、源码分析：

copyOf方法：

```
//返回一个不可变list按顺序包含所给参数，分Iterable是Collection和不是两种情况
public static <E> ImmutableList<E> copyOf(Iterable<? extends E> elements) {
  checkNotNull(elements); // TODO(kevinb): is this here only for GWT?
  return (elements instanceof Collection)
      ? copyOf((Collection<? extends E>) elements)
      : copyOf(elements.iterator());
}
```



```
public static <E> ImmutableList<E> copyOf(Collection<? extends E> elements) {
  if (elements instanceof ImmutableCollection) {
    @SuppressWarnings("unchecked") // all supported methods are covariant
    ImmutableList<E> list = ((ImmutableCollection<E>) elements).asList();
    return list.isPartialView() ? ImmutableList.<E>asImmutableList(list.toArray()) : list;
  }
  //把Collection集合转化为数组在构造一个ImmutableList
  return construct(elements.toArray());
}
```



```
/**
 * 返回一个不可变的list包含给定元素，按顺序
 * @throws NullPointerException if any of {@code elements} is null
 */
public static <E> ImmutableList<E> copyOf(Iterator<? extends E> elements) {
  // We special-case for 0 or 1 elements, but going further is madness.
  if (!elements.hasNext()) {
    return of();
  }
  E first = elements.next();
  if (!elements.hasNext()) {
    return of(first);
  } else {
    //调用Builder<E>()方法构建加入第一个元素再加入所有元素构建
    return new ImmutableList.Builder<E>().add(first).addAll(elements).build();
  }
}
```



```
/**
 * Returns an immutable list containing the given elements, in order.
 *
 * @throws NullPointerException if any of {@code elements} is null
 * @since 3.0
 */
public static <E> ImmutableList<E> copyOf(E[] elements) {
  switch (elements.length) {
    case 0:
      return of();
    case 1:
      return of(elements[0]);
    default:
      //调用元素数组的clone()方法再构造
      return construct(elements.clone());
  }
}
```