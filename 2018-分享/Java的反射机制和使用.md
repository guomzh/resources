### Java的反射机制和使用

反射相信大家平时学习时用的不多但见的很多，特别是各种开源框架中，到此都是反射。编译时加载类是静态加载、运行时加载类是动态加载，动态加载可通过反射实现。

#### 一、定义

* 反射机制是在**运行时**，对于任意一个类，都能够知道这个类的**所有属性和方法**；
* 对于任意一个对象，都能够调用它的**任意一个方法**。
* 在java 中，只要给定**类的名字**，那么就可以通过反射机制来获得类的**所有信息**。

#### 二、功能

* 在运行时判定任意一个对象所属的类；
* 在运行时创建对象；
* 在运行时判定任意一个类所具有的成员变量和方法；
* 在运行时调用任意一个对象的方法；
* 生成动态代理。

如：Class.forName('com.mysql.jdbc.Driver.class');//加载MySql 的驱动类。这就
是反射，现在很多框架都用到反射机制，hibernate，struts ，spring等框架都是用反射机制实
现的。

#### 三、反射的实现方式

1、Class.forName(“类的路径”)
2、类名.class 
3、对象名.getClass()
4、如果是基本类型的包装类，则可以通过调用包装类的Type 属性来获得该包装类的Class 对象。
​      例如：Class<?> clazz = Integer.TYPE;

**这个Class也称类类型，也就是说每一个 Class它也是个对象（万物皆对象），所以clazz是一个类对象**

#### 四、实现反射的类

1、Class：它表示正在运行的Java 应用程序中的类和接口。
2、Field：提供有关类或接口的**属性信息**，以及对它的动态**访问权限**，如private、public等权限。
3、Constructor：提供关于类的单个**构造方法的信息**以及对它的**访问权限**
4、Method：提供关于类或接口中某个**方法信息**。

**注意：Class类是Java反射中最重要的一个功能类，所有获取对象的信息(包括：方法/属性/构造方法/访问权限)都需要它来实现。**

#### 五、Java动态加载类使用场景

例如，你可以传一个参数，运行时判断参数为1加载A类（通过反射），参数为2加载B类，然后A、B类都实现一个接口C，这样就可以面向接口编程啦（主程序面向C编程，后面还可以继续添加C接口的实现类来扩展，符合开闭原则）

#### 六、反射机制的优缺点？

优点：
（1）能够运行时动态获取类的实例，大大提高程序的灵活性(由各框架中到此是反射可见)。
（2）与Java 动态编译相结合，可以实现无比强大的功能。
缺点：
（1）使用反射的性能较低。java 反射是要解析字节码，将内存中的对象进行解析。

#### 七、下面不是概念，干货来啦！

这是我写的反射的一个工具测试类

```
package com.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Guoming Zhang
 * @Description Class类的使用
 * @Date 2018/12/20
 */
class Foo {
    void print(int a, int b) {
        System.out.println(a + b);
    }

    void print(String a, String b) {
        System.out.println(a + " , " + b);
    }
}

public class ReflectUtil {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        /***********一、反射Class类的使用*****************/
        //useOfClass();

        /***********二、反射获取方法信息******************/
        //printMethodMessage(new String());

        /***********三、反射获取成员变量********************************/
        //printFiledMessage(new String());

        /***********四、反射获取成员构造函数********************************/
        //printConstructorMessage(new String());

        /***********五、方法的反射调用********************************/
        //printMethodInvoke();

        /***********六、通过反射验证泛型擦除**********************************************/
        printFanXingClear();
    }

    //反射Class类的使用
    public static void useOfClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Foo foo = new Foo();

        Class c1 = Foo.class;
        Class c2 = foo.getClass();
        Class c3 = Class.forName("com.reflect.Foo");

        System.out.println(c1 == c2);//true
        System.out.println(c2 == c3);//true

        //可以通过类类型创建对象
        Foo myFoo = (Foo) c3.newInstance();
        myFoo.print(1, 2); // "你好啊朋友"
    }

    //反射获取方法信息
    public static void printMethodMessage(Object obj) {
        //获取字节码,或称类类型
        Class c = obj.getClass();
        System.out.println("类的名称为：" + c.getName());//c.getSimpleName()则不带包名

        /**
         * Method : 方法对象，方法也是一个对象！万物皆对象
         * getMethods获取所有public方法，包括父类继承而来的
         * getDeclareMethods() 获取所有自己声明的方法，任何访问权限，不包父类的
         */
        Method[] ms = c.getMethods();//或c.getDeclareMethods()
        for (int i = 0; i < ms.length; i++) {
            //得到方法的返回值类型的类类型，如方法返回一个String，则为String.class
            Class returnType = ms[i].getReturnType();
            //获得类类型的不带包名的名字,getName()则为带包名的名字
            System.out.print(returnType.getSimpleName() + "  ");
            //得到方法的名称
            System.out.print(ms[i].getName() + " (");
            //获取参数类型的类类型
            Class[] paramTypes = ms[i].getParameterTypes();
            for (Class clazz : paramTypes) {
                System.out.print(clazz.getSimpleName() + " , ");
            }
            System.out.println(" )");
        }
    }

    //反射获取成员变量
    public static void printFiledMessage(Object obj) {
        //获取字节码，或称类类型
        Class c = obj.getClass();
        System.out.println("类的名称为：" + c.getName());//c.getSimpleName()则不带包名

        /**
         * 成员变量也是对象，万物皆对象
         * getFields()获取所有public的成员变量信息，包括父类的
         * getDeclaredFields()获取自己声明的所有权限的成员变量信息
         */
        Field[] fs = c.getDeclaredFields();
        for (Field field : fs) {
            //获得成员变量的类型的类类型
            Class fieldType = field.getType();
            //获得成员变量类型的不带包名的名称
            String typeName = fieldType.getSimpleName();
            //获得成员变量的名称
            String fieldName = field.getName();
            System.out.println(typeName + " " + fieldName);
        }
    }

    //反射获取成员构造函数
    public static void printConstructorMessage(Object obj) {
        //获取字节码，或称类类型
        Class c = obj.getClass();
        System.out.println("类的名称为：" + c.getName());//c.getSimpleName()则不带包名

        /**
         * 构造函数也是对象，万物皆对象！
         * getConstructors获取所有public的构造函数，包括父类的
         * getDeclaredConstructors()获取自己声明的所有权限的构造函数，不包父类的
         */
        Constructor[] cs = c.getDeclaredConstructors();
        for (Constructor constructor : cs) {
            System.out.print(constructor.getName() + " （");
            //获取构造函数的参数列表
            Class[] paramTypes = constructor.getParameterTypes();
            for (Class clazz : paramTypes) {
                System.out.print(clazz.getSimpleName() + " ,");
            }
            System.out.println(" )");
        }
    }

    //方法的反射调用
    public static void printMethodInvoke() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Foo foo = new Foo();
        //要获取一个类的方法，先要获取类类型（或称字节码）
        Class c = foo.getClass();
        /**
         * getMethod()获取的是所有public方法，包父类的
         * getDeclaredMethod()获取自己声明的所有权限方法，不包父类的
         */
        Method method = c.getDeclaredMethod("print", int.class, int.class);
        //方法的反射调用 用Method对象来进行调用
        //如果方法没有返回值返回null，否则返回具体的返回值
        Object o = method.invoke(foo, 10, 20);
    }

    //通过反射验证泛型擦除
    public static void printFanXingClear() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ArrayList<String> list = new ArrayList<>();
        list.add("你好");
        /**
         * list.add(45);是不是会出错？
         * 但是！泛型是防止错误输入的，编译后泛型擦除，不存在泛型
         * 下面在运行时通过反射证明
         */
         Class clazz = list.getClass();

         Method method = clazz.getMethod("add",Object.class);
         method.invoke(list,new Integer(45));

        System.out.println(list);
    }
}

```