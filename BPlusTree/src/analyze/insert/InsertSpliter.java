package analyze.insert;

import analyze.Spliter;
import analyze.Stmt;
import analyze.exception.CanNotFindTableException;
import analyze.exception.ExprErrorException;
import analyze.exception.InsertValuesException;

import java.util.ArrayList;
import java.util.List;

public class InsertSpliter implements Spliter {
    @Override
    public InsertStmt split(String sql) throws CanNotFindTableException, ExprErrorException {
        String[] nodes = sql.split(" ");
        List<String> insertInto = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        for (String node : nodes) {
            if(node.equals("insert")){
                continue;
            }
            if(node.equals("into")){
                tmp = insertInto;
                continue;
            }else if(node.equals("values")){
                tmp = values;
                continue;
            }
            tmp.add(node);
        }
        InsertStmt insertStmt = packLists(insertInto,values);
        return insertStmt;
    }

    private InsertStmt packLists(List<String> insertInto, List<String> values) {
        InsertStmt insertStmt = new InsertStmt();
        String tableName = insertInto.get(0);
        String valueString = values.get(0);
        insertStmt.setTableName(tableName);
        insertStmt.setValueString(parseValueString(valueString));
        return insertStmt;
    }

    private String[] parseValueString(String valueString) {
        if(!(valueString.startsWith("(")&&valueString.endsWith(")"))){
            throw new InsertValuesException("插入数值格式错误");
        }
        String[] values = valueString.substring(1, valueString.length() - 1).split(",");
        return values;
    }
}
