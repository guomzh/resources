### Enum

用法一：常量
在JDK1.5 之前，我们定义常量都是： public static final.... 。现在好了，有了枚举，可以把相关的常量分组到一个枚举类型里，而且枚举提供了比常量更多的方法。 

Java代码 

`public enum Color {  
  RED, GREEN, BLANK, YELLOW  
}` 
用法二：switch
JDK1.6之前的switch语句只支持int,char,enum类型，使用枚举，能让我们的代码可读性更强。 

Java代码 

```
enum Signal {  
    GREEN, YELLOW, RED  
}  
public class TrafficLight {  
    Signal color = Signal.RED;  
    public void change() {  
        switch (color) {  
        case RED:  
            color = Signal.GREEN;  
            break;  
        case YELLOW:  
            color = Signal.RED;  
            break;  
        case GREEN:  
            color = Signal.YELLOW;  
            break;  
        }  
    }  
}  
```



用法三：向枚举中添加新方法
如果打算自定义自己的方法，那么必须在enum实例序列的最后添加一个分号。而且 Java 要求必须先定义 enum 实例。 

Java代码 

```
public enum Color {  
    RED("红色", 1), GREEN("绿色", 2), BLANK("白色", 3), YELLO("黄色", 4);  
    // 成员变量  
    private String name;  
    private int index;  
    // 构造方法  
    private Color(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }  
    // 普通方法  
    public static String getName(int index) {  
        for (Color c : Color.values()) {  
            if (c.getIndex() == index) {  
                return c.name;  
            }  
        }  
        return null;  
    }  
    // get set 方法  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public int getIndex() {  
        return index;  
    }  
    public void setIndex(int index) {  
        this.index = index;  
    }  
}  
```



用法四：覆盖枚举的方法
下面给出一个toString()方法覆盖的例子。 

Java代码 

```
public enum Color {  
    RED("红色", 1), GREEN("绿色", 2), BLANK("白色", 3), YELLO("黄色", 4);  
    // 成员变量  
    private String name;  
    private int index;  
    // 构造方法  
    private Color(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }  
    //覆盖方法  
    @Override  
    public String toString() {  
        return this.index+"_"+this.name;  
    }  
}  
```



用法五：实现接口
所有的枚举都继承自java.lang.Enum类。由于Java 不支持多继承，所以枚举对象不能再继承其他类。 

Java代码 

```
public interface Behaviour {  
    void print();  
    String getInfo();  
}  
public enum Color implements Behaviour{  
    RED("红色", 1), GREEN("绿色", 2), BLANK("白色", 3), YELLO("黄色", 4);  
   // 成员变量  
    private String name;  
    private int index;  
    // 构造方法  
    private Color(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }  
//接口方法  
    @Override  
  public String getInfo() {  
       return this.name;  
   }  
   //接口方法  
    @Override  
    public void print() {  
        System.out.println(this.index+":"+this.name);  
    }  

 }


```



#### 一、开发中推荐使用enum代替int常量

```
public enum PlanetType {
    MERCURY(3.302e+23, 2.439e6),    //水星
    EARTH(5.975e+24, 6.378e6),     //地球
    MARS(6.419e+23, 3.393e6);    //火星
    //质量
    private final double mass;
    //半径
    private final double radius;
    //重力加速度
    private final double surfaceGravity;
    //万有引力常数
    private static final double G = 6.67300E-11;

    PlanetType(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double getSurfaceGravity() {
        return surfaceGravity;
    }

    // F = ma
    public double weight(double mass) {
        return mass * surfaceGravity;
    }

}
```

把enum常量和行为联系起来



```
public enum OperationType implements ApplyInterface{
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };
    private final String symbol;

    OperationType(String symbol) {
        this.symbol = symbol;
    }
    @Override
    public String toString() {
        return symbol;
    }
}

```

#### 二、使用示例域代替序数

```
        for(LevelType levelType:LevelType.values()){
            System.out.print(levelType.getStar());
        }
```

```
public enum LevelType {
    VERY_HIGH(5),
    HIGH(4),
    GOOD(3),
    NORMAL(2),
    BAD(1);

    private final int star;

    LevelType(int star) {
        this.star = star;
    }

    public int getStar() {
        return star;
    }
}
```

#### 