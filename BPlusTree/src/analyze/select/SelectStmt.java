package analyze.select;

import analyze.Stmt;
import tableDef.Table;

import java.util.List;

public class SelectStmt implements Stmt {
    /**
     * 目标属性名称的集合
     */
    public List<String> targetList;
    /**
     * 目标表的名称
     */
    public String tableName;

    public Table getT() {
        return t;
    }

    public void setT(Table t) {
        this.t = t;
    }

    /**
     * 目标表对象,在语义检查成功后初始化
     */
    public Table t;
    /**
     * 封装的表达式对象
     */
    public Expr expr;

    public List<String> getTargetList() {
        return targetList;
    }

    public SelectStmt(List<String> targetList, String tableName, Expr expr) {
        this.targetList = targetList;
        this.tableName = tableName;
        this.expr = expr;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setTargetList(List<String> targetList) {
        this.targetList = targetList;
    }



    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public SelectStmt() {
    }
}
