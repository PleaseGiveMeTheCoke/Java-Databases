package analyze;

import analyze.exception.CanNotFindTableException;
import analyze.exception.ExprErrorException;

public interface Spliter {
    Stmt split(String sql) throws CanNotFindTableException, ExprErrorException;
}
