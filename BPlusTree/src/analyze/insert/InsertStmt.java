package analyze.insert;

import analyze.Stmt;
import bplusTree.Tuple;
import tableDef.Table;

public class InsertStmt implements Stmt {
    String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    Tuple key;
    Table table;
    String[] valueString;
    Tuple values;

    public Tuple getKey() {
        return key;
    }

    public void setKey(Tuple key) {
        this.key = key;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String[] getValueString() {
        return valueString;
    }

    public void setValueString(String[] valueString) {
        this.valueString = valueString;
    }

    public Tuple getValues() {
        return values;
    }

    public void setValues(Tuple values) {
        this.values = values;
    }
}
