package dao;

import domain.Car;
import jdbc.JdbcFactory;
import jdbc.JdbcUpdateTemplate;
import jdbc.JdbcUtil;
import jdbc.SqlSession;
import util.MyFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

public class CarDao {

    private String className = "com.mysql.cj.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/test?serverTimezone=CST&characterEncoding=utf8";
    private String username = "root";
    private String password = "root";

    public void Update(Car car) {
        String sql = "insert into t_car(cname, color, price) values(?,?,?)";

        try {
            Class.forName(className);
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement pstat = conn.prepareStatement(sql);
            pstat.setString(1, car.getCname());
            pstat.setString(2, car.getColor());
            pstat.setInt(3, car.getPrice());
            pstat.executeUpdate();
            pstat.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Car> save(Car car) throws SQLException, ClassNotFoundException, InterruptedException {
        String sql = "select * from t_car";
        JdbcUtil util = new JdbcUtil(className, url, username, password);
        List<Car> list = util.selectList(sql, Car.class);
        return list;
    }
    public void save2() throws SQLException, ClassNotFoundException, InterruptedException {
        String sql = "select count(*) from t_car";
        JdbcUtil util = new JdbcUtil(className, url, username, password);
        List<Integer> list = util.selectList(sql, Integer.class);
        System.out.println(list);
    }

    public List<Car> save3() throws SQLException, ClassNotFoundException, InterruptedException {
        String sql = "select * from t_car";
        JdbcFactory factory = new JdbcFactory();
        JdbcUtil util = factory.getUtil();
        List<Car> list = util.selectList(sql, Car.class);
        return list;
    }

    public int save4(Car car) throws SQLException, ClassNotFoundException, InterruptedException {
        String sql = "insert into t_car(cname, color, price) values(?,?,?)";
        JdbcFactory factory = new JdbcFactory();
        JdbcUtil util = factory.getUtil();
        return util.insert(sql, car.getCname(), car.getColor(), car.getPrice());
    }
    public List<Car> save5(Car car) throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, SQLException, IllegalAccessException, InterruptedException {
//        String sql = "insert into t_car(cname, color, price) values(#{cname},#{color},#{price})";
        String sql = "select * from t_car values";
        SqlSession sqlSession = MyFactory.getFactory().getSqlSession();
        System.out.println(sqlSession);
//        JdbcFactory factory = new JdbcFactory();
//        SqlSession sqlSession = factory.getSqlSession();
        List<Car> list = sqlSession.selectList(sql, car, Car.class);
        return list;
    }

    public int delete(int cno) throws ClassNotFoundException, SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InterruptedException {
        String sql = "delete from t_car where cno = #{cno}";
        JdbcFactory factory = new JdbcFactory();
        SqlSession sqlSession = factory.getSqlSession();
        int num = sqlSession.delete(sql, cno);
        return  num;
    }
}
