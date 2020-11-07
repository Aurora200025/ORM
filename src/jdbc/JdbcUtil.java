package jdbc;

import jdbc.pool.ConnectionPool;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 调用增删改查的方法
 */
public class JdbcUtil {

    private String className;
    private String url;
    private String username;
    private String password;

    public JdbcUtil(String className, String url, String username, String password) {
        this.className = className;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * insert 增加语句
     * @param sql 传入的sql语句
     * @param param 可变参数（传值）
     * @return 返回一个整数 代表修改的行数
     */
    public int insert(String sql, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        if (sql.trim().toLowerCase().startsWith("insert")) {
            JdbcUpdateTemplate jdp = new JdbcUpdateTemplate(
                    className,
                    url,
                    username,
                    password
            );
            jdp.setPool(pool);
            return (int) jdp.executeJdbc(sql, param);
        }else {
            throw new NotFoundSqlException("not found insert sql："+sql);
        }
    }

    /**
     *delete 删除语句
     * @param sql 传入的sql语句
     * @param param 可变参数
     * @return 返回一个整数 代表修改的行数
     */
    public int delete(String sql, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        if (sql.trim().toLowerCase().startsWith("delete")) {
            JdbcUpdateTemplate jdp = new JdbcUpdateTemplate(
                    className,
                    url,
                    username,
                    password
            );
            jdp.setPool(pool);
            return (int) jdp.executeJdbc(sql, param);
        }else {
            throw new NotFoundSqlException("not found delete sql："+sql);
        }
    }

    /**
     * update 用来做改数据库信息的语句
     * @param sql 传入的sql语句
     * @param param 可变参数
     * @return 返回一个整数 代表修改的行数
     */
    public int update(String sql, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        if (sql.trim().toLowerCase().startsWith("update")) {
        JdbcUpdateTemplate jdp = new JdbcUpdateTemplate(
                className,
                url,
                username,
                password
        );
        jdp.setPool(pool);
        return (int) jdp.executeJdbc(sql, param);
    }else {
            throw new NotFoundSqlException("not found update sql："+sql);
        }
    }

    /**
     * 策略模式组装成对象
     * @param sql sql语句
     * @param row 策略模式的对象
     * @param param 可变参数
     * @param <T> 泛型
     * @return 返回一个list集合 集合中存一个泛型对象
     * @throws SQLException 报错sql
     * @throws ClassNotFoundException 报错类没有找到
     */
    public <T> List<T> selectList(String sql, RowMapper<T> row, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        if (sql.trim().toLowerCase().startsWith("select")) {
            JdbcQueryTemplate jqt = new JdbcQueryTemplate(
                    className,
                    url,
                    username,
                    password
            );
            jqt.setPool(pool);
            List<Map<String, Object>> map = (List<Map<String, Object>>) jqt.executeJdbc(sql, param);
            List<T> list = new ArrayList<>();
            for (Map<String, Object> m : map) {
                T rows = (T) row.mapping(m);
                list.add(rows);
            }
            return list;
        }else {
            throw new NotFoundSqlException("not found update sql："+sql);
        }
    }

    /**
     * 查询单挑记录
     * @param sql 传入的sql语句
     * @param row 策略对象
     * @param param 可变参数， 一般是dao传进来的
     * @return 返回一个对象
     */
    public <T>T selectOne(String sql, RowMapper<T> row, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        List<T> list = this.selectList(sql, row, param);
        if (list == null || list.size() == 0) {
            return null;
        }else {
            return list.get(0);
        }
    }

    /**
     * 查询无法组装成对象的
     * @param sql 传入的sql语句
     * @param param 可变参数
     * @return 返回一组List集合 但是没有组装成对象
     */
    public List<Map<String, Object>> selectListMap(String sql, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        if (sql.trim().toLowerCase().startsWith("select")) {
            JdbcQueryTemplate jqt = new JdbcQueryTemplate(
                    className,
                    url,
                    username,
                    password
            );
            jqt.setPool(pool);
            List<Map<String, Object>> list = (List<Map<String, Object>>) jqt.executeJdbc(sql, param);
            return list;
        }else {
            throw new NotFoundSqlException("not found update sql："+sql);
        }
    }
    public Map<String, Object> selectMap(String sql, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        List<Map<String, Object>> list = selectListMap(sql, param);
        if (list == null || list.size() == 0) {
            return null;
        }else {
            return list.get(0);
        }
    }

    /**
     * 反射组装对象
     * @param sql 传入sql值
     * @param param 可变参数
     * @param <T> 泛型对象
     * @return 返回一个对象组成的list集合
     */
    public <T> List<T> selectList(String sql,Class<T> type, Object...param) throws SQLException, ClassNotFoundException, InterruptedException {
        if (sql.trim().toLowerCase().startsWith("select")) {
            JdbcQueryTemplate jqt = new JdbcQueryTemplate(
                    className,
                    url,
                    username,
                    password
            );
            jqt.setPool(pool);
            List<Map<String, Object>> rs = (List<Map<String, Object>>) jqt.executeJdbc(sql, param);
            List<T> list = new ArrayList<>();
            try {
                Object row = null;
                for (Map<String, Object> r : rs) {
                    if (type == int.class || type == Integer.class) {
                        Collection con =  r.values();
                        for (Object o : con) {
                            row = ((Number)o).intValue();
                        }
                    }else if (type == long.class || type == Long.class) {
                        Collection con =  r.values();
                        for (Object o : con) {
                            row = ((Number)o).longValue();
                        }
                    }else if (type == double.class || type == Double.class) {
                        Collection con =  r.values();
                        for (Object o : con) {
                            row = ((Number)o).doubleValue();
                        }
                    }else if (type == String.class) {
                        Collection con =  r.values();
                        for (Object o : con) {
                            row = (String)o;
                        }
                    }else {//组成domain实体对象
                        row = type.newInstance();
                        Method[] methods = type.getMethods();
                        for (Method m : methods) {
                            String name = m.getName();
                            if (name.startsWith("set")) {//去掉set
                                name = name.substring(3).toLowerCase();
                                Object value = r.get(name);
                                if (value == null) {
                                    continue;
                                }else {//反射调用方法
                                    Class p = m.getParameterTypes()[0];
                                    if (p == int.class || p == Integer.class) {
                                        m.invoke(row, ((Number)value).intValue());
                                    }else if (p == long.class || p == Long.class) {
                                        m.invoke(row, ((Number)value).longValue());
                                    }else if (p == double.class || p == Double.class) {
                                        m.invoke(row, ((Number)value).doubleValue());
                                    }else if (p == String.class) {
                                        m.invoke(row, (String)value);
                                    }
                                }
                            }
                        }
                    }
                    list.add((T) row);
                }
                return list;
            }catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }else {
            throw new NotFoundSqlException("not found update sql："+sql);
        }
    }
    private ConnectionPool pool;
    public ConnectionPool getPool() {
        return pool;
    }
    public void setPool(ConnectionPool pool) {
        this.pool = pool;
    }
}
