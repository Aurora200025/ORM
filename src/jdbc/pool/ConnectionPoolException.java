package jdbc.pool;

public class ConnectionPoolException extends RuntimeException{

    public ConnectionPoolException() {}
    public ConnectionPoolException (String msg) {
        super(msg);
    }
}
