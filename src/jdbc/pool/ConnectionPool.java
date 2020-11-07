package jdbc.pool;

import javafx.scene.chart.ScatterChart;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {

    //装连接池中的所有的链接
    private List<ConnectionProxy> connections ;
    private ConnectionGenerator generator;
    private String driver;
    private String url;
    private String username;
    private String password;
    //total 链接总数
    private Integer total = 100;
    //最小等待时间数
    private Integer minWait = 2000;
    //最小空闲数 当空闲链接小于这个数目时 变new新的链接
    private Integer minIdle = 5;

    public ConnectionPool(String driver, String url, String username, String password) throws SQLException, ClassNotFoundException {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;

        connections = new ArrayList<>(10);
        Class.forName(driver);
        for (int i = 0; i < 10; i++) {
            Connection connection = DriverManager.getConnection(url, username, password);
            ConnectionProxy cp = new ConnectionProxy(connection);
            connections.add(cp);
        }
        this.generator = new ConnectionGenerator();
        generator.setDaemon(true);
        generator.start();
    }

    public Connection getConnection() throws InterruptedException {
        int wait_time = 0;
        while (true) {
            for (ConnectionProxy cp : connections) {
                if (!cp.getUseFlag()) {
                    synchronized (cp) {
                        if (!cp.getUseFlag()) {
                            //链接是空闲的
                            cp.setUseFlag(true);
                            synchronized (this) {
                                this.notify();
                            }
                            return cp;
                        }
                    }
                }
            }
            Thread.sleep(100);
            wait_time += 100;
            if (wait_time == minWait) {
                throw new ConnectionPoolException("Time limit exceeded!!!");
            }
        }
    }

    private class ConnectionGenerator extends Thread {

        public void checkAndCreate() throws SQLException, InterruptedException {
            while (true) {
                //记录剩余可用链接的数量
                int count = 0;
                for (ConnectionProxy cp : connections) {
                    if (!cp.getUseFlag()) {
                        count++;
                    }
                }
                if (count <= minIdle) {
                    int add_count = 0;
                    if (connections.size() + 10 >= total) {
                        add_count = total - connections.size();
                    }
                    for (int i=1; i<add_count; i++) {
                        Connection conn = DriverManager.getConnection(url, username, password);
                        ConnectionProxy cp = new ConnectionProxy(conn);
                        connections.add(cp);
                    }
                }
                synchronized (this) {
                    this.wait();
                }
            }
        }

        public void run() {
            try {
                checkAndCreate();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getMinWait() {
        return minWait;
    }

    public void setMinWait(Integer minWait) {
        this.minWait = minWait;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }
}
