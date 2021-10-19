package bplusTree;

import java.io.Serializable;

/**
 * B+树
 * @author zhangtianlong
 */
public class BpTree implements Tree, Serializable {

    /**
     * B+树根节点
     */
    BpNode root;

    /**
     * B+树叶子节点的头结点
     */
    BpNode head;

    public BpTree() {
        root = new BpNode(true, true);
        head = root;
    }

    public BpNode getHead() {
        return head;
    }

    public BpNode getRoot() {
        return root;
    }

    @Override
    public Tuple find(Tuple key) {
        return root.get(key);
    }

    @Override
    public void insert(Tuple key,Tuple value) {
        System.out.println("insert " + key.getValues()[0]);
        root.insert(key, value,this);
    }

    @Override
    public boolean remove(Tuple key) {
        return root.remove(key, this);
    }

    /**
     * 验证树本身是否符合B+树规范
     */
    public boolean validate() {
        return root.validate();
    }
}
