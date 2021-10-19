package tableDef;

import bplusTree.BpTree;
import bplusTree.Tuple;
import bplusTree.value.IntValue;
import bplusTree.value.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table implements Serializable {
    private BpTree bPlusTree;
    private String[] colMsg;//格式需为"列名-类型"的格式
    private String[] index;//索引列的序号(从零开始)
    private String tableName;
    private int autoAdd = 0;
    private boolean isAutoIndex = false;


    public String[] getColMsg() {
        return colMsg;
    }

    public String[] getIndex() {
        return index;
    }

    public String getTableName() {
        return tableName;
    }

    public BpTree getbPlusTree() {
        return bPlusTree;
    }

    public Table(String name, String[] colMsg, String[] index){
        this.tableName = name;
        this.colMsg = colMsg;
        this.index = index;
        initTable();
    }
    public Table(String name,String[] colMsg){
        this(name,colMsg,null);
    }


    private void initTable() {
        bPlusTree = new BpTree();
        //用户没有指定索引,需要我们自己生成索引
        if(index==null||index.length==0){
            isAutoIndex = true;
            String autoIndex = "AutoIndex-Integer";
            String[] newColMsg = new String[colMsg.length+1];
            System.arraycopy(colMsg,0,newColMsg,0,colMsg.length);
            newColMsg[colMsg.length] = autoIndex;
            colMsg = newColMsg;
            index = new String[]{colMsg.length-1+""};
        }
    }

    /**
     *
     * @param tuple 插入的数据
     */
    public void insertInTable(Tuple tuple){
        Tuple indexTuple = getIndexCol(tuple, index);


        bPlusTree.insert(indexTuple,tuple);
    }

    /**
     *
     * @param tuples 待删除数据的索引元组
     */
    public boolean deleteFromTable(Tuple[] tuples){
        for (Tuple tuple : tuples) {
            if(!bPlusTree.remove(tuple)){
                return false;
            }
        }
        return true;

    }

    /**
     *
     * @param tuples 待查找数据的索引元组
     * @return
     */
    public Tuple[] selectFromTable(Tuple[] tuples){
        Tuple[] res = new Tuple[tuples.length];
        int index = 0;
        for (Tuple tuple : tuples) {
            res[index] = bPlusTree.find(tuple);
            index++;
        }
        return res;
    }


    private Tuple getIndexCol(Tuple tuple, String[] index) {
        if(isAutoIndex){
            Value[] values = new Value[1];
            values[0] = new IntValue(autoAdd);
            autoAdd++;
            return new Tuple(values);
        }
        Value[] values = tuple.getValues();
        Value[] res = new Value[index.length];
        int m = 0;
        for (String s : index) {
            int i = Integer.parseInt(s);
            res[m] = values[i];
            m++;
        }
        return new Tuple(res);
    }



}
