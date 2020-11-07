package jdbc;

import jdbc.annocations.Delete;
import jdbc.annocations.Insert;
import jdbc.annocations.Select;
import jdbc.annocations.Update;
import jdbc.pool.ConnectionPool;

import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 增删改查 本质上还是jdbcUtil
 * 不过得先经过SqlHandler预处理sql语句
 */
public class SqlSession {

    JdbcUtil util;
    public SqlSession(String driver, String url, String username, String password, ConnectionPool pool) {
        util = new JdbcUtil(driver, url, username, password);
        util.setPool(pool);
    }
    public int insert(String sql, Object param) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException, InterruptedException {
        SqlAndParam sp = SqlHandler.execute(sql, param);
        return util.insert(sp.getSql(), sp.getParams().toArray());
    }
    public int insert(String sql) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException, InterruptedException {
        return util.insert(sql);
    }

    public int update(String sql, Object param) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        SqlAndParam sp = SqlHandler.execute(sql, param);
        return util.update(sp.getSql(), sp.getParams().toArray());
    }
    public int update(String sql) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        return util.update(sql);
    }

    public int delete(String sql, Object param) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        SqlAndParam sp = SqlHandler.execute(sql, param);
        return util.delete(sp.getSql(), sp.getParams().toArray());
    }
    public int delete(String sql) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        return util.delete(sql);
    }
    public List<Map<String, Object>> selectListMap(String sql , Object param) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        SqlAndParam sp = SqlHandler.execute(sql, param);
        return util.selectListMap(sp.getSql(), sp.getParams().toArray());
    }
    public List<Map<String, Object>> selectListMap(String sql) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        return util.selectListMap(sql);
    }

    public Map<String, Object> selectMap(String sql , Object param) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        SqlAndParam sp = SqlHandler.execute(sql, param);
        return util.selectMap(sp.getSql(), sp.getParams().toArray());
    }
    public Map<String, Object> selectMap(String sql ) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        return util.selectMap(sql);
    }

    public <T> List<T> selectList(String sql , Object param, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException, InterruptedException {
        SqlAndParam sp = SqlHandler.execute(sql, param);
        return util.selectList(sp.getSql(),type, sp.getParams().toArray());
    }
    public <T> List<T> selectList(String sql, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException, InterruptedException {
        return util.selectList(sql, type);
    }

    public <T> List<T> selectOne(String sql , Object param, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException, InterruptedException {
        SqlAndParam sp = SqlHandler.execute(sql, param);
        return util.selectList(sp.getSql(), type, sp.getParams().toArray());
    }public <T> List<T> selectOne(String sql, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException, InterruptedException {
        return util.selectList(sql, type);
    }

    /**
     * 根据dao的规则 来创建一个dao对应的实现类
     * @param daoInterface 类
     * @param <T> 泛型
     * @return 返回那个实现类
     */
    @SuppressWarnings("all")
    public <T> T createDaoImp(Class<T> daoInterface) {
        return (T) Proxy.newProxyInstance(
                daoInterface.getClassLoader(),
                new Class[]{daoInterface},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //当业务调用dao接口方法时 实际上调用invoke方法
                        //所以对于业务来说 业务想执行什么操作，就在当前的invoke方法中写相应的操作
                        /*
                         * proxy 当前代理类产生的代理对象，不要（反射）应用，会产生栈内存溢出
                         * method 当前调用的方法 dao.save() ---> method 表示 save
                         * args 调用方法时传递的参数 dao.save(Car, book) --> args{Car}
                         */
                        System.out.println(method.getName());
                        Annotation anno = method.getAnnotations()[0];
                        Method m = anno.getClass().getMethod("value");
                        String sql = (String) m.invoke(anno);
                        Object obj = args == null ? null : args[0];
                        //获得了本次对应业务操作的sql和参数param（obj）
                        Object result = null;
                        if (anno.annotationType() == Insert.class) {
                            if (obj == null) {
                                result = insert(sql);
                            }else {
                                result = insert(sql, obj);
                            }
                        }else if (anno.annotationType() == Delete.class) {
                            if (obj == null) {
                                result = delete(sql);
                            }else {
                                result = delete(sql, obj);
                            }
                        } else if (anno.annotationType() == Update.class) {
                            if (obj == null) {
                                result = update(sql);
                            }else {
                                result = update(sql, obj);
                            }
                        } else if (anno.annotationType() == Select.class) {
                            Class rt = method.getReturnType();
                            if (rt == List.class) {
                                Type type = method.getGenericReturnType();
                                ParameterizedType pt = (ParameterizedType) type;
                                Class fx = (Class) pt.getActualTypeArguments()[0];
                                if (fx == Map.class) {
                                    if (obj == null) {
                                        result = selectListMap(sql);
                                    }else {
                                        result = selectListMap(sql, obj);
                                    }
                                } else {
                                    if (obj == null) {
                                    result = selectList(sql, fx);
                                    }else {
                                        result = selectList(sql, obj, fx);
                                    }
                                }
                            } else {
                                if (rt == Map.class) {
                                    if (obj == null) {
                                        result = selectMap(sql);
                                    }else {
                                        result = selectMap(sql, obj);
                                    }
                                } else {
                                    if (obj == null) {
                                        result = selectOne(sql, rt);
                                    }else {
                                        result = selectOne(sql, obj, rt);
                                    }
                                }
                            }
                        }
                        return result;
                    }
                }
        );
    }

    private ConnectionPool pool;
    public ConnectionPool getPool() {
        return pool;
    }
    public void setPool(ConnectionPool pool) {
        this.pool = pool;
    }
}
