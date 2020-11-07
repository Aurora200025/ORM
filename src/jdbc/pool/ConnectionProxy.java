package jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionProxy extends AbstractConnection{

    //false 不关闭； true关闭
    private Boolean useFlag = false;
    //false 空闲 ； true使用
    private Boolean closeFlag = false;

    public ConnectionProxy(Connection connection) {
        super.connection = connection;
    }

    public void close() throws SQLException {
        if (closeFlag) {
            connection.close();
        }else {
            useFlag = false;
        }
    }

    public Boolean getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(Boolean useFlag) {
        this.useFlag = useFlag;
    }

    public Boolean getCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(Boolean closeFlag) {
        this.closeFlag = closeFlag;
    }
}
