package jdbc;

import jdbc.pool.ConnectionPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * 用来获取jdbcUtil对象和SqlSession对象
 */
public class JdbcFactory {

    private String driver;
    private String url;
    private String username;
    private String password;
    private ConnectionPool pool;
    private Integer total ;
    private Integer minWait ;
    private Integer minIdle ;

    public JdbcFactory() {
        this("mysql.properties");
    }
    public JdbcFactory(String fileName) {
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource(fileName)).getPath();
        File file = new File(path);
        readFile(file);
    }
    public JdbcFactory(File file) {
        readFile(file);
    }
    private void readFile(File file) {
        try {
            InputStream input = new FileInputStream(file);
            Properties p = new Properties();
            p.load(input);
            driver = p.getProperty("driver");
            url = p.getProperty("url");
            username = p.getProperty("username");
            password = p.getProperty("password");
            String total = p.getProperty("total");
            String minWait = p.getProperty("minWait");
            String minIdle = p.getProperty("minIdle");
            if (total != null && !"".equalsIgnoreCase(total)) {
                this.total = Integer.parseInt(total);
            }
            if (minWait != null && !"".equalsIgnoreCase(minWait)) {
                this.minWait = Integer.parseInt(minWait);
            }
            if (minIdle != null && !"".equalsIgnoreCase(minIdle)) {
                this.minIdle = Integer.parseInt(minIdle);
            }
            this.createConnectionPool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JdbcUtil getUtil() {
        JdbcUtil util = new JdbcUtil(driver, url, username, password);
        util.setPool(pool);
        return util;
    }

    public SqlSession getSqlSession() {
        SqlSession session = new SqlSession(driver, url, username, password, pool);
        session.setPool(pool);
        return session;
    }

    private void createConnectionPool() throws SQLException, ClassNotFoundException {
        pool = new ConnectionPool(driver, url, username, password);
        pool.setTotal(200);
        pool.setMinWait(2000);
        pool.setMinIdle(5);
    }
}
