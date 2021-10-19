package analyze.select;

import analyze.*;
import analyze.exception.CanNotFindTableException;
import analyze.exception.ExprErrorException;

import java.util.ArrayList;
import java.util.List;

public class SelectSpliter implements Spliter {


    @Override
    public SelectStmt split(String sql) throws CanNotFindTableException, ExprErrorException {
       String[] nodes = sql.split(" ");
       List<String> select = new ArrayList<>();
       List<String> from = new ArrayList<>();
       List<String> where = new ArrayList<>();
       List<String> tmp = new ArrayList<>();
        for (String node : nodes) {
            if(node.equals("select")){
                tmp = select;
                continue;
            }else if(node.equals("from")){
                tmp = from;
                continue;
            }else if(node.equals("where")){
                tmp = where;
                continue;
            }
            tmp.add(node);
        }
        SelectStmt selectStmt =  packLists(select,from,where);
        return selectStmt;
    }

    private SelectStmt packLists(List<String> select, List<String> from, List<String> where) throws CanNotFindTableException, ExprErrorException {
        SelectStmt selectStmt = new SelectStmt();
        String tableName = from.get(0);
        Expr expr = parseExpr(where);
        selectStmt.setExpr(expr);
        selectStmt.setTableName(tableName);
        selectStmt.setTargetList(select);
        return selectStmt;
    }

    /**
     * 该方法用来解析表达式,目前只支持简单的譬如"a>10"的解析
     * 对于逻辑,也只支持一层逻辑运算,即a>10&&b<5
     * 对于括号的解析,以及对于多层逻辑符的解析,由于
     * 我能力暂时有限(需要用到正则表达式相关的知识)
     * 所以暂时搁置
     * @param exprNodes
     * @return
     * @throws ExprErrorException
     */
    private Expr parseExpr(List<String> exprNodes) throws ExprErrorException {
        if(exprNodes==null||exprNodes.size()==0){
            return null;
        }
        if(exprNodes.size()<3){
            throw new ExprErrorException("表达式格式错误");
        }
        Expr expr = new Expr();
        String attribute = exprNodes.get(0);
        String sign = exprNodes.get(1);
        String constant = exprNodes.get(2);
        expr.setAttribute(attribute);
        expr.setConstant(constant);
        expr.setSign(sign);
        if(exprNodes.size()==3){
            return expr;
        }
        exprNodes.remove(0);
        exprNodes.remove(0);
        exprNodes.remove(0);
        if(exprNodes.get(0).equals("or")){
            exprNodes.remove(0);
            expr.setOr(parseExpr(exprNodes));
        }else if(exprNodes.get(0).equals("and")){
            exprNodes.remove(0);
            expr.setAnd(parseExpr(exprNodes));
        }else{
            throw new ExprErrorException("表达式解析错误");
        }
        return expr;
    }

    public static void main(String[] args) throws ExprErrorException {
        List<String> exprNodes = new ArrayList<>();
        exprNodes.add("id");
        exprNodes.add(">");
        exprNodes.add("3");
        exprNodes.add("||");
        exprNodes.add("name");
        exprNodes.add("==");
        exprNodes.add("wmj");
        SelectSpliter s = new SelectSpliter();

        Expr expr = s.parseExpr(exprNodes);
        System.out.println(expr);
    }
}
