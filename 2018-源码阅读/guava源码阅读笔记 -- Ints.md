####  guava源码阅读笔记 -- Ints

com.google.common.primitives：八种原始类型和无符号类型的静态工具包

Ints里面有一个静态不可变类IntConverter extends Converter<String, Integer>，通过doForward()和doBackward()方法来实现String和Integer类型的相互转化

```
Ints.stringConverter().convert(a)
```

* 把一个int转为一个大端表示的4元素字节数组，通过右移位把对应位转化为对应字节，
  如0x12131415转化为{0x12, 0x13, 0x14, 0x15}数组

```
public static byte[] toByteArray(int value) {
    return new byte[] {
            (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value
    };
}
```

* 把一个大端表示的4字节数组转化为一个int值，通过左移位完成

```
public static int fromByteArray(byte[] bytes) {
    checkArgument(bytes.length >= BYTES, "array too small: %s < %s", bytes.length, BYTES);
    return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
}
public static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
 }
```

* 通过StringBuilder来把给定的参数列表int数组转化为用分隔符separator连起来的字符串

```
public static String join(String separator, int... array) {
    checkNotNull(separator);
    if (array.length == 0) {
        return "";
    }
    // For pre-sizing a builder, just get the right order of magnitude
    StringBuilder builder = new StringBuilder(array.length * 5);
    builder.append(array[0]);
    for (int i = 1; i < array.length; i++) {
        builder.append(separator).append(array[i]);
    }
    return builder.toString();
}
```

* 把一个int数组转化为一个底层为数组的List,这个List继承自AbstractList<Integer>

```
public static List<Integer> asList(int... backingArray) {
    if (backingArray.length == 0) {
        return Collections.emptyList();
    }
    return new IntArrayAsList(backingArray);
}
```



    //数组间比较器 比较条件：将数组元素通过index一一对应比较，直到某个index的元素按字典序较大即为对应数组较大，如果两数组前缀相同则比较长度，这种用枚举类实现的方法值得借鉴
    public static Comparator<int[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
     
    private enum LexicographicalComparator implements Comparator<int[]> {
        //使用枚举类实现单例,调用LexicographicalComparator.INSTANCE就可以进行访问。
        //相比较于返回new对象，更快。相比全局变量，不影响全局空间。
        INSTANCE;
     
        @Override
        public int compare(int[] left, int[] right) {
            int minLength = Math.min(left.length, right.length);
            for (int i = 0; i < minLength; i++) {
                int result = Ints.compare(left[i], right[i]);
                if (result != 0) {
                    return result;
                }
            }
            return left.length - right.length;
        }
     
        @Override
        public String toString() {
            return "Ints.lexicographicalComparator()";
        }
    }
    
