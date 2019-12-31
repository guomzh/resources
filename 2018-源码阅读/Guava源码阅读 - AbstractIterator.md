### Guava源码阅读 - AbstractIterator

在阅读guava中AbtractIterator抽象类源码的时候，发现它在guava中的设计是为了解决**只迭代集合中某些类型的数据源**，它提供了一个抽象的实现，在使用的时候只要重写computeNext()方法即可。

如源码中提到的例子：

```
 * public static Iterator<String> skipNulls(final Iterator<String> in) {
 *   return new AbstractIterator<String>() {
 *     protected String computeNext() {
 *       while (in.hasNext()) {
 *         String s = in.next();
 *         if (s != null) {
 *           return s;
 *         }
 *       }
 *       return endOfData();
 *     }
 *   };
 * }
 * }
```

```
@GwtCompatible
public abstract class AbstractIterator<T> extends UnmodifiableIterator<T> {
  private State state = State.NOT_READY;

  /** Constructor for use by subclasses. */
  protected AbstractIterator() {}
  //状态枚举
  private enum State {
    /** We have computed the next element and haven't returned it yet. */
    READY,

    /** We haven't yet computed or have already returned the element. */
    NOT_READY,

    /** We have reached the end of the data and are finished. */
    DONE,

    /** We've suffered an exception and are kaput. */
    FAILED,
  }

  private @Nullable T next;


   //计算下一个元素
  protected abstract T computeNext();

  /**
   * Implementations of {@link #computeNext} <b>must</b> invoke this method when there are no
   * elements left in the iteration.
   *
   * @return {@code null}; a convenience so your {@code computeNext} implementation can use the
   *     simple statement {@code return endOfData();}
   */
   //到底数据结尾，把枚举状态设置为done
  @CanIgnoreReturnValue
  protected final T endOfData() {
    state = State.DONE;
    return null;
  }

  @CanIgnoreReturnValue // TODO(kak): Should we remove this? Some people are using it to prefetch?
  //先检查枚举状态不为异常或则迭代完，再进行操作：如果没计算出next结果就返回计算后的next结果
  @Override
  public final boolean hasNext() {
    checkState(state != State.FAILED);
    switch (state) {
      case DONE:
        return false;
      case READY:
        return true;
      default:
    }
    return tryToComputeNext();
  }

  private boolean tryToComputeNext() {
    state = State.FAILED; // temporary pessimism
    next = computeNext();
    if (state != State.DONE) {
      state = State.READY;
      return true;
    }
    return false;
  }

  @CanIgnoreReturnValue // TODO(kak): Should we remove this?
  @Override
  //获取下一个值
  public final T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    state = State.NOT_READY;
    T result = next;
    next = null;
    return result;
  }

  /**
   * Returns the next element in the iteration without advancing the iteration, according to the
   * contract of {@link PeekingIterator#peek()}.
   *
   * <p>Implementations of {@code AbstractIterator} that wish to expose this functionality should
   * implement {@code PeekingIterator}.
   */
   //获取下一个值，但不把迭代器置到下一位
  public final T peek() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return next;
  }
}
```