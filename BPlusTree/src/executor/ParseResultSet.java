package executor;

import analyze.select.SelectStmt;
import bplusTree.Tuple;
import bplusTree.value.Value;
import tableDef.Table;

import java.util.List;

public class ParseResultSet {
    ResultSet resultSet;
    SelectStmt selectStmt;
    Table table;
    public ParseResultSet(ResultSet resultSet,SelectStmt selectStmt){
        this.resultSet = resultSet;
        this.selectStmt = selectStmt;
        table = selectStmt.getT();
    }
    public void doParse(){
        List<String> targetList = selectStmt.targetList;
        if(targetList.get(0).equals("*")){
            String[] colMsg = table.getColMsg();
            String attribute = "";
            for (String s : colMsg) {
                attribute += s.split("-")[0]+"-";
            }
            attribute = attribute.substring(0,attribute.length()-1);
            resultSet.attributes = attribute;
            return;
        }
        String attribute = "";
        for (String s : targetList) {
            attribute = attribute+s+"-";
        }
        attribute = attribute.substring(0,attribute.length()-1);
        resultSet.attributes = attribute;
        int len = targetList.size();
        int[] indexes = new int[len];
        int i = 0;
        for (String s : targetList) {
            int index = getIndexOfAttribute(s);
            indexes[i] = index;
            i++;
        }

        for (int j = 0; j < resultSet.datas.size(); j++) {
            resultSet.datas.set(j, parseTuple(resultSet.datas.get(j),indexes));
        }
    }

    private Tuple parseTuple(Tuple data, int[] indexes) {
        Value[] datas = data.getValues();
        Value[] value = new Value[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            value[i] = datas[indexes[i]];
        }
        return new Tuple(value);
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


}
