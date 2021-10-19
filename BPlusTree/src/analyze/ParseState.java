package analyze;

import analyze.exception.CanNotFindTableException;
import analyze.exception.InsertValuesException;
import analyze.insert.InsertStmt;
import analyze.select.Expr;
import analyze.select.SelectStmt;
import bplusTree.Tuple;
import bplusTree.value.*;
import tableDef.Table;
import util.FileUtil;
import util.Seri;

import java.util.*;

public class ParseState {
    private final String BIGGER = ">";
    private final String LESS = "<";
    private final String EQUAL = "=";
    private final String BIGGER_OR_EQUAL = ">=";
    private final String LESS_OR_EQUAL = "<=";
    private final String NOT_EQUAL = "!=";
    Set<String> tableNames;
    HashMap<String,Set<String>> attributes;
    HashMap<String,List<String>> types;
    Set<String> expressions;
    Table t;

    public ParseState() throws CanNotFindTableException {
        initParseState();
    }

    /**
     * 对传入的selectStmt执行语义检查
     * 同时
     * @return
     * @throws CanNotFindTableException
     */
    public boolean doSelectTest(SelectStmt selectStmt) throws CanNotFindTableException {
        selectStmt.setT(getTable(selectStmt.tableName));
        return testExpr(selectStmt.expr)&&testTargetList(selectStmt.targetList);
    }

    /**
     * 对传入的insertStmt执行语义检查
     * @param insertStmt
     * @return
     */

    public boolean doInsertTest(InsertStmt insertStmt) throws CanNotFindTableException {
        insertStmt.setTable(getTable(insertStmt.getTableName()));
        testValues(insertStmt.getValueString(),insertStmt);
        setKeys(insertStmt.getValues(),insertStmt);
        return true;
    }

    private void setKeys(Tuple values, InsertStmt insertStmt) {
        String[] index = t.getIndex();
        Value[] value = values.getValues();
        Value[] key = new Value[index.length];
        for (int i = 0; i < index.length; i++) {
            key[i] = value[Integer.parseInt(index[i])];
        }
        insertStmt.setKey(new Tuple(key));
    }

    /**
     * 对valueString的属性合法化进行检查
     * 如果一切正常,会生成对应Tuple赋值到insertStmt中
     * @param valueString
     * @return
     */
    private void testValues(String[] valueString,InsertStmt insertStmt) throws CanNotFindTableException {
        List<String> valueTypes = types.get(t.getTableName());
        if(valueTypes.size()!=valueString.length){
            throw new InsertValuesException("插入数据个数与表定义不符");
        }
        Value[] values = new Value[valueString.length];
        for (int i = 0; i < valueString.length; i++) {
            String expectedType = valueTypes.get(i);
            String value = valueString[i];
            values[i] = testType(expectedType,value);
        }
        insertStmt.setValues(new Tuple(values));
    }

    private Value testType(String expectedType, String value) {
        try {
            switch (expectedType) {
                case "int":
                    int v = Integer.valueOf(value);
                    return new IntValue(v);
                case "String":
                    return new StringValue(value);
                case "boolean":
                    boolean b = Boolean.valueOf(value);
                    return new BooleanValue(b);
                case "long":
                    long l = Long.valueOf(value);
                    return new LongValue(l);
            }
            return null;
        }catch (NumberFormatException e){
            throw new InsertValuesException("插入数据类型与表定义不符");
        }
    }


    /**
     * 对该语义检查的辅助类进行初始化
     * 主要功能为
     * 1.获得表名称文件的表名信息
     * 2.将表名称与该表所含参数列表进行一一对应
     * 3.初始化合法的表达式集合
     * @throws CanNotFindTableException
     */

    private void initParseState() throws CanNotFindTableException {
        tableNames = getNamesFromFile("tableNames.txt");
        attributes = getAttributesMap();
        expressions = initExpressions();
        types = getTypesMap();
    }

    private Set<String> initExpressions() {
        Set<String> expressions = new HashSet<>();
        expressions.add(BIGGER);
        expressions.add(LESS);
        expressions.add(LESS_OR_EQUAL);
        expressions.add(BIGGER_OR_EQUAL);
        expressions.add(EQUAL);
        expressions.add(NOT_EQUAL);
        return expressions;
    }

    /**
     * 建立一个"表名-表中属性名集合"的映射关系"
     * @return
     * @throws CanNotFindTableException
     */
    private HashMap<String, Set<String>> getAttributesMap() throws CanNotFindTableException {
        HashMap<String, Set<String>> res = new HashMap<>();
        for (String tableName : tableNames) {
            Set<String> attributes = new HashSet<>();
            Table t = getTable(tableName);
            String[] colMsg = t.getColMsg();
            for (String s : colMsg) {
                String colName = s.split("-")[0];
                attributes.add(colName);

            }
            res.put(tableName,attributes);
        }
        return res;
    }
    /**
     * 建立一个"表名-表中类型名集合"的映射关系"
     * @return
     * @throws CanNotFindTableException
     */
    private HashMap<String, List<String>> getTypesMap() throws CanNotFindTableException {
        HashMap<String, List<String>> res = new HashMap<>();
        for (String tableName : tableNames) {
            List<String> attributes = new ArrayList<>();
            Table t = getTable(tableName);
            String[] colMsg = t.getColMsg();
            for (String s : colMsg) {
                String colName = s.split("-")[1];
                attributes.add(colName);
            }
            res.put(tableName,attributes);
        }
        return res;
    }

    /**
     * 对该表达式进行合法性的检查
     * 检查事项包括
     * 1.左侧的参数是否在表中包含
     * 2.中间的符号是否合法
     *
     * @param e
     * @return
     */
    public boolean testExpr(Expr e){
        if(e==null){
            return true;
        }
        String attribute = e.attribute;
        String tableName = t.getTableName();
        Set<String> tableAttributes = attributes.get(tableName);
        if(!tableAttributes.contains(attribute)){
            return false;
        }
        String sign = e.sign;
        if(!expressions.contains(sign)){
            return false;
        }
        return true;
    }

    /**
     * 根据用户select语句中from后的字段得到对应表
     * 如果在表名称文件中找不到对应表则抛出异常
     * @param tableName
     * @return
     * @throws CanNotFindTableException
     */
    public Table getTable(String tableName) throws CanNotFindTableException {
        try {
             t = Seri.deSerialize(tableName+"Def.txt");
        } catch (Exception e) {
            throw new CanNotFindTableException("找不到对应表");
        }
        return t;
    }


    /**
     * 对目标属性进行检查
     * 主要判断其是否包含于该表定义的属性中
     * @param targetList 所要查找的目标属性集合
     * @return
     */
    public boolean testTargetList(List<String> targetList){
        if(targetList.size()==1&&targetList.get(0).equals("*")){
            return true;
        }
        Set<String> tableAttributes = attributes.get(t.getTableName());
        for (String s : targetList) {
            if(!tableAttributes.contains(s)){
                return false;
            }
        }
        return true;

    }

    /**
     * 得到表名称文件中的信息
     * 以集合的形式返回
     * @param fileName
     * @return
     */
    private Set<String> getNamesFromFile(String fileName) {
        List<String> names = FileUtil.readFileByLines(fileName);
        Set<String> res = new HashSet<>();
        for (String name : names) {
            res.add(name);
        }
        return res;
    }


}
