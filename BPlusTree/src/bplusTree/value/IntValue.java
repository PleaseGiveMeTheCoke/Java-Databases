package bplusTree.value;

import java.io.Serializable;

/**
 * @author zhangtianlong
 */
public class IntValue extends Value implements Serializable {

    private int i;



    public IntValue() {
    }

    public IntValue(int i) {
        this.i = i;

    }


    public int getInt() {
        return i;
    }

    public void setInt(int i) {
        this.i = i;
    }

    @Override
    public int getLength() {
        return 1 + 4;
    }

    @Override
    public byte getType() {
        return INT;
    }

    @Override
    public int compare(Value value) {
        int result = ((IntValue) value).getInt();
        if (i > result) {
            return 1;
        } else if (i == result) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String getValue() {
        return i+"";
    }

    @Override
    public String toString() {
        return String.valueOf(i);
    }
}
