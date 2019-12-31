#### Guava源码阅读 - Strings 

##### 一、重要api作用

* nullToEmpty(String string)  如果是null转为空串

* String emptyToNull(String string)  如果是空串转为null

* isNullOrEmpty(String string)   判读是不是Null或者空串

* String padStart(String string, int minLength, char padChar) 填补开头，达到给定的长度，已达到则不填补

* String padEnd(String string, int minLength, char padChar) 填补结尾，达到给定的长度，已达到则不填补

* String repeat(String string, int count) 返回count个string串的复制连接起来

* String commonPrefix(CharSequence a, CharSequence b) 返回公共前缀，没有返回空串

* String commonSuffix(CharSequence a, CharSequence b) 返回公共后缀，没有返回空串

* checkArgument(count >= 0, "invalid count: %s", count);  在Preconditinos类中

* String lenientFormat(
  ​    @Nullable String template, @Nullable Object @Nullable... args) 把 "arg is %s "类型的模板用参数列表格式化

#### 二、源代码鉴赏

1、通过系统底层System.arraycopy()方式复制数组，最后把数组构建一个String，实现字符串复制功能

```
public static String repeat(String string, int count) {
    checkNotNull(string); // eager for GWT.
    //检查一个条件，不正确跑出非法参数异常，异常信息可格式化定义
    if (count <= 1) {
      checkArgument(count >= 0, "invalid count: %s", count);
      return (count == 0) ? "" : string;
    }

    // IF YOU MODIFY THE CODE HERE, you must update StringsRepeatBenchmark
    final int len = string.length();
    final long longSize = (long) len * (long) count;
    final int size = (int) longSize;
    if (size != longSize) {
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
    }
    //先把已有字符串复制到数组的前部分
    final char[] array = new char[size];
    string.getChars(0, len, array, 0);
    int n;
    //每次复制从已存在部分开始，即n=len，复制完一次则已有数组长度会翻倍，这时长度可以通过左移一位来得到，如果已有长度大与剩下需复制的长度size-n,则退出循环，再进行一次复制操作即可
    for (n = len; n < size - n; n <<= 1) {
      System.arraycopy(array, 0, array, n, n);
    }
    //因为剩下需复制的长度小于已有数组长度了，所以从0开始到剩下的长度结束这一段复制到后面即可
    System.arraycopy(array, 0, array, n, size - n);
    return new String(array);
  }
```

二、

* StringBuilder最好给一个长度值，append可以只apend一个字符串的指定开始结束下标之间部分

* 想在StringBuilder前面追加可以先append追加部分，再append已有字符串

```
StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
builder.append(template, templateStart, placeholderStart);
builder.append(args[i++]);
```

