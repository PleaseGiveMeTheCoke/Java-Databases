package analyze;

import analyze.exception.CanNotFindTableException;
import analyze.exception.ExprErrorException;
import analyze.insert.InsertSpliter;
import analyze.insert.InsertStmt;
import analyze.select.SelectSpliter;
import analyze.select.SelectStmt;
import bplusTree.Tuple;
import bplusTree.value.IntValue;
import bplusTree.value.StringValue;
import bplusTree.value.Value;
import executor.InsertExecutor;
import executor.ParseResultSet;
import executor.ResultSet;
import executor.SelectQuery;
import server.Shower;
import tableDef.Table;
import util.Seri;

import java.io.IOException;
import java.util.Random;

public class Analyzer {
    public boolean OperateTypeAnalyzer(String sql) throws CanNotFindTableException, ExprErrorException {
        switch (sql.charAt(0)){
            //select
            case 's':
               if(!AnalyzeSelect(sql)){
                    return true;
               }
                break;
            //delete
            case 'd':
                if(!AnalyzeDelete(sql)){
                    return true;
                }
                break;
            //insert
            case 'i':
                if(!AnalyzeInsert(sql)){
                    return true;
                }

                break;
            //update
            case 'u':
                break;
        }
        return false;
    }

    private boolean AnalyzeDelete(String sql) throws ExprErrorException, CanNotFindTableException {
      sql = sql.replace("delete","select *");
      SelectSpliter selectSpliter = new SelectSpliter();
      SelectStmt selectStmt= selectSpliter.split(sql);
      ParseState tester = new ParseState();
        if(tester.doSelectTest(selectStmt)){
            SelectQuery selectQuery = new SelectQuery(selectStmt);
            ResultSet query = selectQuery.query(0);
            try {
                Seri.Serialize(selectStmt.t,selectStmt.tableName+"Def.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private boolean AnalyzeInsert(String sql) throws ExprErrorException, CanNotFindTableException {
        InsertSpliter insertSpliter = new InsertSpliter();
        InsertStmt insertStmt = insertSpliter.split(sql);
        ParseState tester = new ParseState();
        if(tester.doInsertTest(insertStmt)){
            InsertExecutor insertExecutor = new InsertExecutor(insertStmt);
            insertExecutor.doInsert();
            //插入成功后要进行序列化表的操作
        }
        return true;
    }


    public boolean AnalyzeSelect(String sql) throws CanNotFindTableException, ExprErrorException {
        SelectSpliter selectSpliter = new SelectSpliter();
        SelectStmt selectStmt= selectSpliter.split(sql);
        ParseState tester = new ParseState();
        if(tester.doSelectTest(selectStmt)){
            SelectQuery selectQuery = new SelectQuery(selectStmt);
            ResultSet query = selectQuery.query(1);
            ParseResultSet parseResultSet = new ParseResultSet(query,selectStmt);
            parseResultSet.doParse();
            Shower shower = new Shower(query);
            System.out.println(shower.show());
            return true;
        }
        return false;
    }
    public boolean deleteAnalyzer(String sql){
        return false;
    }
    public boolean insertAnalyzer(String sql){
        return false;
    } public boolean updateAnalyzer(String sql){
        return false;
    }

    public static void main(String[] args) throws ExprErrorException, IOException {
        Analyzer analyzer = new Analyzer();
        //refresh();
        //analyzer.OperateTypeAnalyzer("insert into Person values (wmj,1000,男,19)");
        //analyzer.OperateTypeAnalyzer("select * from Person where id = 56");
        //analyzer.OperateTypeAnalyzer("delete from Person where id = 56");
        for (int i = 5; i < 100; i++) {
            analyzer.OperateTypeAnalyzer("delete from Person where id = "+i);
            System.out.println("正在删除第"+i+"号");
        }
        //analyzer.OperateTypeAnalyzer("select 姓名 性别 from Person where id > 5 and id < 10");

        //


    }

    private static void refresh() throws IOException {
        //表信息,格式为 "列名-类型"
        String[] colMsg ={"姓名-String","id-int","性别-String","age-int"};
        //构造方法的参数1:表名   参数2  索引列所在序号
        Table table = new Table("Person",colMsg,new String[]{"1"});
        ;

        for (int i = 5; i <100 ; i++) {
            StringValue s = new StringValue("机器人"+i+"号");
            IntValue ii = new IntValue(i);
            StringValue sad = null;
            if(Math.random()>0.5) {
                sad = new StringValue("男");
            }else{
                sad = new StringValue("女");
            }
            IntValue age = new IntValue(new Random().nextInt(100));
            Value[] v = new Value[]{s,ii,sad,age};
            Tuple tad = new Tuple(v);
            table.insertInTable(tad);
        }


        Seri.Serialize(table,"PersonDef.txt");

    }


}
