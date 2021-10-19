package analyze.select;

public class Expr {
    /***
     * 需要进行范围限制的参数
     */
    public String attribute;

    /**
     * 限制符号
     * 包括 >, <, =, !=, <=, >=
     */
    public String sign;

    /**
     * 限制符号右侧的常量
     */
    public String constant;

    /**
     * 和该表达式有"与"关系的表达式指针
     */
    public Expr and;

    /**
     * 和该表达式有"或"关系的表达式指针
     */
    public Expr or;

    public Expr() {
    }

    @Override
    public String toString() {
        return "Expr{" +
                "attribute='" + attribute + '\'' +
                ", sign='" + sign + '\'' +
                ", constant='" + constant + '\'' +
                ", and=" + and +
                ", or=" + or +
                '}';
    }

    public Expr(String attribute, String sign, String constant, Expr and, Expr or) {
        this.attribute = attribute;
        this.sign = sign;
        this.constant = constant;
        this.and = and;
        this.or = or;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public Expr getAnd() {
        return and;
    }

    public void setAnd(Expr and) {
        this.and = and;
    }

    public Expr getOr() {
        return or;
    }

    public void setOr(Expr or) {
        this.or = or;
    }
}
