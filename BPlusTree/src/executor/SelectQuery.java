package executor;

import analyze.select.Expr;
import analyze.select.SelectStmt;
import bplusTree.BpNode;
import bplusTree.BpTree;
import bplusTree.Tuple;
import bplusTree.value.*;
import tableDef.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 查找思路:
 * 首先,查找分两种情况,一是走索引,二是不走索引
 * 对于不走索引的查找,即用户where后面传来的条件中不包含索引
 * 我们要做的就是对BpTree的叶子节点从左往右进行遍历
 * 对于走索引的查找,我们直接调用BpTree的find方法进行查找
 *
 * 对于where后的条件,根据最左匹配原则,我们只需要判断第一个式子的参数能否
 * 走索引即可.
 *
 * 针对每次查找,我们可以先将不等符号统统转化为等于符号,再根据找到的目标Node
 * 结合不同的符号进行划分取舍.
 *
 */
public class SelectQuery implements Query {
    //该查询是否走索引,0为不走索引,1长度为走单一索引,2长度为走联合索引
    int isPassIndex;
    Table table;
    BpTree tree;
    SelectStmt selectStmt;
    public SelectQuery(SelectStmt selectStmt){
        this.selectStmt = selectStmt;
        table = selectStmt.t;
        tree = table.getbPlusTree();
    }

    /**
     *
     * @param expr
     * @param table
     * @return String[],"属性名-类型-值"的格式
     */
    private int judgeIsPassIndex(Expr expr, Table table) {
        if(expr==null){
            return 0;
        }
        //1.只要sign中包含"!=",则不走索引
        if(expr.sign.equals("!=")||
                (expr.and!=null&&expr.and.sign.equals("!="))||
                (expr.or!=null&&expr.or.sign.equals("!="))
        ){
            return 0;
        }
        //2.判断第一个参数是否可以走索引
        String attribute1 = expr.attribute;
        int index1 = getIndexOfAttribute(attribute1);
        String[] indexes = table.getIndex();
        if(index1==Integer.parseInt(indexes[0])){
            //3.判断有几个attribute
            if(expr.and==null&&expr.or==null){
                return 1;
            }else{
                //4.判断两个表达式之间的关系
                if(expr.and!=null){
                    //关系为and
                    //5.判断索引数量
                    if(indexes.length==1){
                        return 1;
                    }else{
                        //6.判断第二个参数是否也是索引
                        String attribute2 = expr.and.attribute;
                        int index2 = getIndexOfAttribute(attribute2);
                        if(index2==Integer.parseInt(indexes[1])){
                            //7.判断两个sign均为"="
                            if(expr.sign.equals("=") && expr.and.sign.equals("=")){
                                return 2;
                            }else{
                                return 1;
                            }
                        }else{
                            return 1;
                        }
                    }
                }else{
                    //关系为or
                    return 0;
                }
            }
        }else{
            return 0;
        }
    }

    private int getIndexOfAttribute(String attribute) {
        int index = -1;
        String[] colMsg = table.getColMsg();
        for (int i = 0; i < colMsg.length; i++) {
            if(colMsg[i].split("-")[0].equals(attribute)){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public ResultSet query(int sign) {
        isPassIndex = judgeIsPassIndex(selectStmt.expr,table);
        if(isPassIndex==0){
            // 全表扫描
          return doQuery(selectStmt.expr,sign);
        }else{
            // 走索引
            Tuple index = getIndexTuple(isPassIndex);
            return doQuery(index,selectStmt.expr,isPassIndex,sign);
        }
    }

    private ResultSet doQuery(Expr expr,int sign) {

        ResultSet resultSet = new ResultSet();
        BpNode head = tree.getHead();
        Set<Tuple> tuples1 = new HashSet<>();
        Set<Tuple> tuples2 = new HashSet<>();
        Set<Tuple> res = new HashSet<>();
        for(;head!=null;head = head.getNext()){
            List<Tuple> entries = head.getEntries();
            for (Tuple entry : entries) {
                if(testExpr(entry,expr)){
                    tuples1.add(entry);
                    if(expr==null){
                        continue;
                    }
                }
                if(testExpr(entry,expr.and)){
                    tuples2.add(entry);
                }
                if(testExpr(entry,expr.or)){
                    tuples2.add(entry);
                }

            }
        }
        if(expr!=null&&expr.and!=null){
            for (Tuple tuple : tuples1) {
                if(tuples2.contains(tuple)){
                    res.add(tuple);
                }
            }
        }else if(expr!=null&&expr.or!=null){
           res.addAll(tuples1);
           res.addAll(tuples2);
        }else{ 
           res.addAll(tuples1);
        }
        if(sign==1) {
            resultSet.addAll(res);
        }else{
            for (Tuple re : res) {
                tree.remove(re);
            }
        }
        return resultSet;
    }

    private ResultSet doQuery(Tuple index, Expr expr,int isPassIndex,int sign) {
        ResultSet resultSet = new ResultSet();
        Tuple targetTuple = tree.find(index);
        if(targetTuple==null){
           return doQuery(expr,sign);
        }
        if(isPassIndex==2){
           resultSet.add(targetTuple);
           return resultSet;
        }else{
            List<Tuple> res = new ArrayList<>();
            if(expr.sign.equals("=")){
                res.add(targetTuple);
            }else if(expr.sign.equals(">")){
                 ArrayList<Tuple> tuples = getAllBigger(targetTuple);
                 res.addAll(tuples);
            }else if(expr.sign.equals(">=")){
                ArrayList<Tuple> tuples = new ArrayList<>();
                tuples.add(targetTuple);
                tuples.addAll(getAllBigger(targetTuple));
                res.addAll(tuples);
            }else if(expr.sign.equals("<")){
                ArrayList<Tuple> tuples = getAllLesser(targetTuple);
                res.addAll(tuples);
            }else if(expr.sign.equals("<=")){
                ArrayList<Tuple> tuples = new ArrayList<>();
                tuples.add(targetTuple);
                tuples.addAll(getAllLesser(targetTuple));
                res.addAll(tuples);
            }
            //判断是否包含and条件
            if(expr.and!=null){
                Expr and = expr.and;
                // TODO 根据and条件剔除res中的部分元素
                ArrayList<Tuple> ar = new ArrayList<>();
                for (Tuple re : res) {

                    if(testExpr(re,and)){
                        ar.add(re);
                    }
                }
                if(sign==1) {
                    resultSet.addAll(ar);
                }else{
                    for (Tuple tuple : ar) {
                        tree.remove(tuple);
                    }
                }
                return resultSet;
            }else{
                if(sign==1) {
                    resultSet.addAll(res);
                }else{
                    for (Tuple re : res) {
                        tree.remove(re);
                    }
                }
               return resultSet;
            }
        }
    }

    private  boolean testExpr(Tuple re, Expr and) {
        if(and==null){
            return true;
        }
        Tuple data = re.getData();
        String attribute = and.attribute;
        int indexOfAttribute = getIndexOfAttribute(attribute);
        Value[] values = data.getValues();
        String value = values[indexOfAttribute].getValue();
        Byte type =  values[indexOfAttribute].getType();
        String constant = and.constant;
        if(type==4){
            if(!and.sign.equals("=")){
                System.out.println("布尔类型无法比较");
                return false;
            }else {
                return constant.equals(value);
            }
        }else{
            switch (and.sign){
                case ">":
;                   if(type!=1){
                        return Long.parseLong(value)>Long.parseLong(constant);
                    }
                    return value.compareTo(constant)>0;
                case ">=":
                    if(type!=1){
                        return Long.parseLong(value)>=Long.parseLong(constant);
                    }
                    return value.compareTo(constant)>=0;
                case"<":
                    if(type!=1){
                        return Long.parseLong(value)<Long.parseLong(constant);
                    }
                    return value.compareTo(constant)<0;
                case "<=":
                if(type!=1){
                    return Long.parseLong(value)<=Long.parseLong(constant);
                }
                    return value.compareTo(constant)<=0;
                case "=":
                    return value.compareTo(constant)==0;
                case "!=":
                    return !(value.compareTo(constant)==0);

            }
            System.out.println("比较符号异常");
            return false;
        }

    }

    public static void main(String[] args) {
        /**
         * 模拟数据:
         *    id  姓名   年龄    性别
         *    id为索引
         */
        Value[] v = new Value[]{new IntValue(1)};
        Tuple t = new Tuple(v);
        Value[] v2 = new Value[]{new IntValue(1)
        ,new StringValue("王明军")
        ,new IntValue(19)
        ,new StringValue("男")};
        Tuple t2 = new Tuple(v2);
        t.setData(t2);
        Expr expr = new Expr();
        expr.setConstant("王明军2");
        expr.setSign("=");
        expr.setAttribute("年龄");

    }

    private ArrayList<Tuple> getAllLesser(Tuple targetTuple) {
        ArrayList<Tuple> res = new ArrayList<>();
        BpNode thisNode = targetTuple.getNode();
        List<Tuple> entries = thisNode.getEntries();
        int index = 0;
        if(entries==null) {
            return res;
        }
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i) == targetTuple) {
                    index = i;
                    break;
                }
            }
            for (int i = index - 1; i >= 0; i--) {
                res.add(entries.get(i));
            }

        BpNode cur = tree.getHead();
        for(;cur!=thisNode;cur = cur.getNext()){
            res.addAll(cur.getEntries());
        }
        return res;

    }

    private ArrayList<Tuple> getAllBigger(Tuple targetTuple) {
        ArrayList<Tuple> res = new ArrayList<>();
        BpNode thisNode = targetTuple.getNode();
        List<Tuple> entries = thisNode.getEntries();
        int index = 0;
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i)==targetTuple){
                index = i;
                break;
            }
        }
        for (int i = index+1; i <entries.size() ; i++) {
            res.add(entries.get(i));
        }
        BpNode cur = thisNode.getNext();
        for(;cur!=null;cur = cur.getNext()){
            res.addAll(cur.getEntries());
        }
        return res;
    }



    private Tuple getIndexTuple(int isPassIndex) {

        String[] indexes = table.getIndex();
        String[] colMsg = table.getColMsg();
        int index1 = Integer.parseInt(indexes[0]);
        String msg = colMsg[index1];
        String type = msg.split("-")[1];
        String value = selectStmt.expr.constant;
        Value values1 = getValue(type, value);
       if(isPassIndex==1){
           return new Tuple(new Value[]{values1});
       }else{
           int index2 = Integer.parseInt(indexes[1]);
           String msg2 = colMsg[index2];
           String type2 = msg2.split("-")[1];
           String value2 =selectStmt.expr.and.constant;
           Value values2 = getValue(type2, value2);
           return new Tuple(new Value[]{values1,values2});
       }
    }

    private Value getValue(String type, String value) {
        switch (type){
            case "int": return new IntValue(Integer.parseInt(value));
            case "String": return new StringValue(value);
            case "boolean":return new BooleanValue(Boolean.parseBoolean(value));
            case "long":return new LongValue(Long.valueOf(value));
        }
        System.out.println("类型出错");
        return null;

    }
}
