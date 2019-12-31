#### Guava源码阅读 -   Splitter

##### 一、概览

Splitter主要是用来做字符串切分处理，它的一个典型的使用场景为：

```
Splitter.on(',')
    .trimResults()
    .omitEmptyStrings()
    .split("foo,bar,,   qux");
```

这将返回一个trim掉空格，忽略空串的Iterable<String>变量，返回结果为[foo, bar, qux]

#####  二、使用方法与使用场景

###### 静态工厂方法：

| 方法                                            | 描述                                               | 例子                                                         |
| ----------------------------------------------- | -------------------------------------------------- | ------------------------------------------------------------ |
| Splitter.on(char)                               | 基于特定字符划分                                   | `Splitter.on(';')`                                           |
| Splitter.on(CharMatcher)                        | 基于某些类别划分                                   | `Splitter.on(';')`                                           |
| Splitter.on(String)                             | 基于字符串划分                                     | `Splitter.on(CharMatcher.BREAKING_WHITESPACE)` `Splitter.on(CharMatcher.anyOf(";,."))` |
| Splitter.on(Pattern) Splitter.onPattern(String) | 基于正则表达式划分                                 | `Splitter.on(", ")`                                          |
| Splitter.fixedLength(int)                       | 按指定长度划分，最后部分可以小于指定长度但不能为空 | `Splitter.fixedLength(3)`                                    |

###### 修改器

| 方法                     | 描述                                                         | 例子                                                         |
| ------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| omitEmptyStrings()       | 移去结果中的空字符串                                         | `Splitter.on(',').omitEmptyStrings().split("a,,c,d")` 返回 `"a", "c", "d"` |
| trimResults()            | 将结果中的空格删除，等价于`trimResults(CharMatcher.WHITESPACE)` | `Splitter.on(',').trimResults().split("a, b, c, d")` 返回 `"a", "b", "c", "d"` |
| trimResults(CharMatcher) | 移除匹配字符                                                 | `Splitter.on(',').trimResults(CharMatcher.is('_')).split("_a ,_b_ ,c__")` 返回 `"a ", "b_ ", "c"` |
| limit(int)               | 达到指定数目后停止字符串的划分                               | `Splitter.on(',').limit(3).split("a,b,c,d")` 返回 `"a", "b", "c,d"` |

###### Splitter.MapSplitter

这段代码中withKeyValueSeparator将返回一个Joiner.MapJoiner对象，它有一个public Map<String, String> split(CharSequence sequence)方法，再调用spilt()方法返回一个map

Map : {"1":"2", "3":"4"}

```
Splitter.on("#").withKeyValueSeparator(":").split("1:2#3:4");
```

一般推荐使用 JSO而不是MapJoiner和MapSplitter`。

##### 三、源码分析

###### 成员变量

主要的成员变量有：

```
private final CharMatcher trimmer;
private final boolean omitEmptyStrings;
private final Strategy strategy;
private final int limit;
```

CharMatcher变量实现了Predicate<Character>借口，

###### 策略模式和模板方法模式的运用：

Splitter类的构造方法需要传入一个策略接口，Strategy是它的一个内部私有策略接口，用于提供字符串分割后的迭代器

```
private Splitter(Strategy strategy) {
  this(strategy, false, CharMatcher.none(), Integer.MAX_VALUE);
}
```

```
private interface Strategy {
  Iterator<String> iterator(Splitter splitter, CharSequence toSplit);
}
```

以Splitter on(final CharMatcher separatorMatcher)函数为例，这里返回的是SplittingIterator(它是个抽象类，继承了 AbstractIterator，而 AbstractIterator继承了 Iterator）。

```
public static Splitter on(final CharMatcher separatorMatcher) {
  checkNotNull(separatorMatcher);

  return new Splitter(
      new Strategy() {
        @Override
        public SplittingIterator iterator(Splitter splitter, final CharSequence toSplit) {
          return new SplittingIterator(splitter, toSplit) {
            @Override
            int separatorStart(int start) {
              return separatorMatcher.indexIn(toSplit, start);
            }

            @Override
            int separatorEnd(int separatorPosition) {
              return separatorPosition + 1;
            }
          };
        }
      });
}
```

SplittingIterator需要覆盖实现 separatorStart和 separatorEnd两个方法才能实例化。这两个方法也是 SplittingIterator 用到的模板方法模式的重要组成。

###### 惰性迭代器与模板方法的使用：

上面提到的SplittingIterator实现了AbstractIterator<String>接口

```
@GwtCompatible
abstract class AbstractIterator<T> implements Iterator<T> {
  private State state = State.NOT_READY;

  protected AbstractIterator() {}
  //使用枚举变量保存当前的枚举状态，如准备，未准备，跌倒完成，失败出错
  private enum State {
    READY,
    NOT_READY,
    DONE,
    FAILED,
  }
  //枚举的下一个值
  private @Nullable T next;
  //定义的模板方法
  protected abstract T computeNext();
  //计算完成，改变迭代器状态
  @CanIgnoreReturnValue
  protected final @Nullable T endOfData() {
    state = State.DONE;
    return null;
  }

  @Override
  public final boolean hasNext() {
    //先保守第置为失败状态，自我保护
    checkState(state != State.FAILED);
    switch (state) {
      case READY:
        return true;
      case DONE:
        return false;
      default:
    }
    //对computeNext()方法的封装使用
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

  @Override
  public final T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    state = State.NOT_READY;
    T result = next;
    next = null;
    return result;
  }

  @Override
  public final void remove() {
    throw new UnsupportedOperationException();
  }
}
```

###### SplittingIterator

SplittingIterator还是一个抽象类，虽然实现了 computeNext 方法，但是它又定义了两个抽象函数:

-  separatorStart： 返回分隔符在指定下标之后第一次出现的下标
-  separatorEnd： 返回分隔符在指定下标后面第一个不包含分隔符的下标。

它的computeNext方法进行计算下一个值

```
@Override
    protected String computeNext() {
      // 返回的字符串介于上一个分隔符和下一个分隔符之间。
      // nextStart 是返回子串的起始位置，offset 是下次开启寻找分隔符的地方。 
      int nextStart = offset;
      while (offset != -1) {
        int start = nextStart;
        int end;

        // 找offset 之后第一个分隔符出现的位置
        int separatorPosition = separatorStart(offset);
        if (separatorPosition == -1) {
          // 处理没找到的情况
          end = toSplit.length();
          offset = -1;
        } else {
          // 处理找到的情况
          end = separatorPosition;
          offset = separatorEnd(separatorPosition);
        }
        
        // 处理的是第一个字符就是分隔符的特殊情况
        if (offset == nextStart) {
          // 发生情况：空字符串
          offset++;
          if (offset > toSplit.length()) {
            offset = -1;
          }
          continue;
        }
        // 根据 trimmer 来对找到的元素做前处理，比如去除空白符之类的。
        while (start < end && trimmer.matches(toSplit.charAt(start))) {
          start++;
        }
        // 根据trimmer来对找到的元素做后处理，比如去除空白符之类的。
        while (end > start && trimmer.matches(toSplit.charAt(end - 1))) {
          end--;
        }
        // 根据需要去除那些是空字符串的元素，trim完之后变成空字符串的也会被去除。
        if (omitEmptyStrings && start == end) {
          // Don't include the (unused) separator in next split string.
          nextStart = offset;
          continue;
        }
        // 判断 limit，limit表示要分割成几段，如果只剩1段了，那剩下的字符串就作为一个整体
        if (limit == 1) {
             
          end = toSplit.length();
          // 调整 end 指针的位置标记 offset 为 -1，下一次再调用 computeNext 
          // 的时候就发现 offset 已经是 -1 了，然后就返回 endOfData 表示迭代结束。
          offset = -1;
          // Since we may have changed the end, we need to trim it again.
          while (end > start && trimmer.matches(toSplit.charAt(end - 1))) {
            end--;
          }
        } else {
          // 还没到 limit 的极限，就让 limit 自减
          limit--;
        }
        return toSplit.subSequence(start, end).toString();
      }
      return endOfData();
    }
  }
```