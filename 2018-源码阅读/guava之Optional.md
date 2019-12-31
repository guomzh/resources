#### guava之Optional
#### 一、开发中有几个常见问题：

1、大多数集合类不希望存nul

2、null具有二义性，语义不清晰，如map.get(key)为null可以表示key对应的value是null，也可表示这个key不存在

3、大多数情况检查Null是不得不做的，但有时会忘记。空指针异常很讨厌

鉴于此，Optional应运而生：

定义：Optional接口来使null快速失败，即在可能为null的对象上做了一层封装，在使用Optional静态方法of时，如果传入的参数为null就抛出NullPointerException异常。

#### 二、怎么获得Optional实例：

1、Optional.of(T)：获得一个Optional对象，其内部包含了一个非null的T数据类型实例，若T=null，则在运行时抛异常。

2、Optional.absent()：获得一个Optional对象，其内部包含了null(即空值)。

3、Optional.fromNullable(T)：将一个T的实例转换为Optional对象，T的实例可以不为空，也可以为空；即Optional.fromNullable(null)和Optional.absent()是等价的

#### 三、几个常用的方法

1. boolean isPresent()：如果Optional包含的T实例不为null，则返回true；若T实例为null，返回false

2. T get()：返回Optional包含的T实例，该T实例必须不为空；否则，对包含null的Optional实例调用get()会抛出一个IllegalStateException异常

3. T or(T)：若Optional实例中包含了T类型实例，返回Optional包含的该T实例，否则返回输入的T实例作为默认值

4. T orNull()：返回Optional实例中包含的非空T实例，如果Optional中包含的是空值，返回null，逆操作是fromNullable()

5. Set asSet()：返回一个不可修改的Set，该Set中包含Optional实例中包含的所有非空存在的T实例，且在该Set中，每个T实例都是单态，如果Optional中没有非空存在的T实例，返回的将是一个空的不可修改的Set。

####  四、一个例子：计算所有员工的年龄

非Optional的java代码，要手动判空，要是忘记判空了岂不是讨厌？：

    @Test
    public void test() {
        List<Employee> employees = Lists.newArrayList(new Employee("A",21),
                new Employee("B",22),
                null,
                new Employee("C",23)
                );
        int employeeAgeSum = 0;
        for(Employee e :employees){
            if(e!=null){
                employeeAgeSum+=e.getAge();
            }
        }
        System.out.println("employee age sum:"+employeeAgeSum);
    
    }
使用Optional的guava代码：

    for(Employee e :employees){
          employeeAgeSum+=Optional.fromNullable(e).
                       or(newEmployee("default",0)).getAge();
    
    }
#### 五、总结：

**Optional迫使你积极思考引用缺失的情况，因为你必须显式地从Optional获取引用。**Optional就是一种傻瓜式强制提醒你判空的一种方式。可以将参数设置为Optional类型，也可以将方法的返回类型指定为Optional，这样就可以迫使调用者思考返回的引用缺失的情形。

