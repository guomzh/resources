import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//不同货币汇率转换作业
//主程序类，输入格式：不同参数用空格隔开如 1 RMB = 2 USD
public class MoneyTransferCalc {
    //存所有的货币
    List<Money> list = new ArrayList<>();

    //存不同货币之间的汇率
    Table<Money, Money, Double> rateTable = HashBasedTable.create();

    //通过别名获得这种货币
    public Money getMoneyWithName(String name) {
        for (Money money : list) {
            if (money.getNames().contains(name))
                return money;
        }
        return null;
    }

    //处理指令入口
    public void solve() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入有效指令：");
        while (scanner.hasNextLine()) {
            try {
                String str = scanner.nextLine();
                if (str.equals("clear")) {
                    list.clear();
                    rateTable.clear();
                    System.out.println("所有汇率已清除，请输入新指令：");
                    continue;
                }
                String[] input = str.split("=");
                //输入指令不包含=号，不合法
                if (input.length == 1) {
                    System.out.println("输入指令不合法，请重新输入:");
                    continue;
                }
                //把输入指令根据 "=" 划分为前后两部分参数，每部分参数又分为参数部分num，货币名部分name
                String num1;
                String num2;
                String name1;
                String name2;
                String[] items = input[0].trim().split("\\s+");
                if (items[0].charAt(0) >= '0' && items[0].charAt(0) <= '9') {
                    num1 = items[0];
                    name1 = items[1];
                } else {
                    num1 = items[1];
                    name1 = items[0];
                }
                Money money1 = getMoneyWithName(name1);
                //循环处理连“=”操作,分别对“=”分开的第1部分参数和第i部分参数做指令处理
                for (int i = 1; i < input.length; i++) {
                    items = input[i].trim().split("\\s+");
                    if (items[0].charAt(0) >= '0' && items[0].charAt(0) <= '9' || items[0].charAt(0) == '?' || items[0].charAt(0) == '？') {
                        num2 = items[0];
                        name2 = items[1];
                    } else {
                        num2 = items[1];
                        name2 = items[0];
                    }
                    Money money2 = getMoneyWithName(name2);
                    //处理别名录入
                    if (num1.equals("1") && num2.equals("1")) {
                        if (money1 == null) {
                            money1 = new Money();
                            money1.getNames().add(name1);
                            money1.getNames().add(name2);
                            list.add(money1);
                        } else {
                            money1.getNames().add(name2);
                        }
                    }
                    //输入指令不包含?号，做汇率录入操作
                    else if (!str.contains("?") && !str.contains("？")) {
                        if (money1 == null) {
                            money1 = new Money();
                            money1.getNames().add(name1);
                            list.add(money1);
                        }
                        if (money2 == null) {
                            money2 = new Money();
                            money2.getNames().add(name2);
                            list.add(money2);
                        }
                        double rate1 = Double.valueOf(num2) / Double.valueOf(num1);
                        double rate2 = Double.valueOf(num1) / Double.valueOf(num2);
                        rateTable.put(money1, money2, rate1);
                        rateTable.put(money2, money1, rate2);
                    }
                    //处理结果计算，输出“?”处数值等于多少
                    else if (str.contains("?") || str.contains("？")) {
                        double rate = rateTable.get(money1, money2);
                        System.out.printf("%.4f", rate * Double.valueOf(num1));
                        System.out.println(" " + name2);

                    } else {
                        System.out.println("错误");
                        System.out.println("请重新输入：");
                    }
                }

            } catch (Exception e) {
                System.out.println("指令语法错误。");
                System.out.println("请重新输入：");
            }
        }
    }

    public static void main(String[] args) {
        //程序入口
        MoneyTransferCalc calc = new MoneyTransferCalc();
        calc.solve();
    }
}
