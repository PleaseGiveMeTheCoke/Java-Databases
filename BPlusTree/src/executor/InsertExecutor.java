package executor;

import analyze.insert.InsertStmt;
import tableDef.Table;
import util.Seri;

import java.io.IOException;

public class InsertExecutor {
    InsertStmt insertStmt;
    public InsertExecutor(InsertStmt insertStmt){
        this.insertStmt = insertStmt;
    }
    public void doInsert(){
        Table t = insertStmt.getTable();
        t.getbPlusTree().insert(insertStmt.getKey(),insertStmt.getValues());
        try {
            Seri.Serialize(t,t.getTableName()+"Def.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
