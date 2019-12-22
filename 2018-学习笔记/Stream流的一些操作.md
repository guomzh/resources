##### Stream流的一些操作

* collect收集成List或Set等    collect(Collectors.toList()); collect(Collectors.joining(", ", "{", "}"))收集成字符串

* map转换   map(k -> k.toUpperCase());

* count个数   count()

* filter遍历并筛选并计数    list.stream().filter(p -> p.startsWith("j")).count()；

* flatmap将多个流转换为一个流   Stream.of(Arrays.asList(1,2,4),Arrays.asList(1,2,45)).flatMap(k -> k.stream())

* max、min终止操作   

  ```
  String[] testStrings = { "java", "react", "angular", "javascript", "vue" };
  Optional<String> max = Stream.of(testStrings).max(Comparator.comparing(String::length));
  ```

* reduce终止操作（从Stream的一组值中生产另一个值，max、min、count实际上都是reduce操作）

```
int sum = Stream.of(1, 2, 3, 4).reduce(0, (accumulator, element) ->         accumulator + element);
```

​    上述例子初始值为0，做了累加操作，accumulator为累加器，element为流中的每个元素

* anymatch匹配返回boolean值

  ```
  boolean result = listIds.stream().anyMatch(p->p.equals("2"));
  ```