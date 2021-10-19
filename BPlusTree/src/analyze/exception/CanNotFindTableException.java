package analyze.exception;

import java.io.IOException;

public class CanNotFindTableException extends IOException {
    public CanNotFindTableException(String msg){
        super(msg);
    }
}
