package jdbc;

public class NotFoundSqlException extends RuntimeException {
    public NotFoundSqlException() {}
    public NotFoundSqlException(String msg) {
        super(msg);
    }
}
