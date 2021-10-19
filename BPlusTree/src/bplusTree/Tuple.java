package bplusTree;


import bplusTree.value.Value;

import java.io.Serializable;

/**
 * 元组
 *
 * @author zhangtianlong
 */
public class Tuple implements Serializable,Comparable<Tuple> {

    Value[] values;

    public BpNode getNode() {
        return node;
    }

    public void setNode(BpNode node) {
        this.node = node;
    }

    //仅限叶子节点,非叶子节点该引用始终指向空
    Tuple data;

    public Tuple getData() {
        return data;
    }

    public void setData(Tuple data) {
        this.data = data;
    }

    //该key对应的叶子节点
    BpNode node;

    public Tuple() {

    }

    public Tuple(Value[] values) {
        this.values = values;
    }

    public Value[] getValues() {
        return values;
    }

    public void setValues(Value[] values) {
        this.values = values;
    }

    public int getLength() {
        int sum = 0;
        for (Value value : values) {
            sum += value.getLength();
        }
        return sum;
    }

    /**
     * 联合索引比较的时候,先比较第一个索引值,若相等则再比较下一个索引值,依次类推
     */
    public int compare(Tuple tuple) {
        int min = values.length < tuple.values.length ? values.length : tuple.values.length;
        for (int i = 0; i < min; i++) {
            int comp = values[i].compare(tuple.values[i]);
            if (comp == 0) {
                continue;
            }
            return comp;
        }
        int res = values.length - tuple.values.length;
        return (res == 0) ? 0 : (res > 1 ? 1 : -1);
    }

    @Override
    public int compareTo(Tuple o) {
        return this.compare(o);
    }
}
