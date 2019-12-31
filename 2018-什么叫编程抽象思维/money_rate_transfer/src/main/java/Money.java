import java.util.HashSet;
import java.util.Set;

//货币抽象，一种货币可以有不同别名
public class Money {

    public static int i = 1;

    //该种货币别名
    Set<String> names;

    //该种货币的唯一Key值
    int moneyKey;

    public Money() {
        names = new HashSet<>();
        this.moneyKey = i;
        i++;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public int getMoneyKey() {
        return moneyKey;
    }

    public void setMoneyKey(int moneyKey) {
        this.moneyKey = moneyKey;
    }

    @Override
    public int hashCode() {
        return moneyKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.hashCode() == obj.hashCode()) {
            return true;
        }
        return false;
    }

}
