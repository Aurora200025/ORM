package jdbc;

import jdbc.pool.ConnectionPool;

import java.sql.*;

public abstract class JdbcTemplate {

    private String className;
    private String url;
    private String username;
    private String password;
    private Connection conn;
    protected PreparedStatement preparedStatement;
    private ResultSet rs;
    private ConnectionPool pool;

    public JdbcTemplate(String className, String url, String username, String password) {
        this.className = className;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Object executeJdbc(String sql, Object[] param) throws ClassNotFoundException, SQLException, InterruptedException {
        One();
        Two();
        Three();
        Four(sql, param);
        Object object = Five();
        Six();
        return object;
    }

    /**
     * 导包
     */
    private void One() {}

    /**
     * 加载驱动类
     */
    private void Two() throws ClassNotFoundException {
        //如果在连接池中做了，那么加载驱动也不用做了
//        Class.forName(className);
    }

    /**
     * 创建连接池
     */
    private void Three() throws SQLException{
//        conn = DriverManager.getConnection(url, username, password);
        try {
            conn = pool.getConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建预处理对象
     * @param sql sql语句
     * @param param 用户写的值
     */
    private void Four(String sql, Object[] param) throws SQLException {
        preparedStatement = conn.prepareStatement(sql);
        for (int i=0; i<param.length; i++) {
            preparedStatement.setObject(i+1, param[i]);
        }
    }
    protected abstract Object Five() throws SQLException;

    private void Six() throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    public ConnectionPool getPool() {
        return pool;
    }
    public void setPool(ConnectionPool pool) {
        this.pool = pool;
    }
}
