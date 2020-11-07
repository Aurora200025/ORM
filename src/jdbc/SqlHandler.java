package jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理带有 #sql 的sql语句
 */
public class SqlHandler {

    public static SqlAndParam execute(String sql, Object param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<String> keys = new ArrayList<>();
        while (true) {
            StringBuilder builder = new StringBuilder();
            int i1 = sql.indexOf("#{");
            int i2 = sql.indexOf("}");
            if (i1 == -1 || i2 == -1 || i1 > i2) {
                break;
            }
            String key = sql.substring(i1 + 2, i2).trim();
            keys.add(key);
            if (i2 == sql.length() - 1) {
                builder.append(sql.substring(0, i1));
                builder.append("?");
                sql = builder.toString();
//                sql = sql.substring(0, i1)+"?";
                break;
            }else {
                builder.append(sql.substring(0, i1));
                builder.append("?");
                builder.append(sql.substring(i2+1));
                sql = builder.toString();
//                sql = sql.substring(0, i1) + "?"+sql.substring(i2+1);
            }
        }
        List<Object> list = new ArrayList<>();
        Class clazz = param.getClass();
        if (clazz==int.class || clazz==Integer.class || clazz==double.class || clazz==Double.class || clazz==String.class) {
            //sql只有一个
            list.add(param);
        }else if(clazz == Map.class || clazz == HashMap.class) {
            for (String key : keys) {
                Map map = (Map) param;
                Object value = map.get(key);
                list.add(value);
            }
        }else {//是一个对象
            for (String key : keys) {
                String name = "get"+key.substring(0, 1).toUpperCase()+key.substring(1);
                Method method = clazz.getMethod(name);
                Object value = method.invoke(param);
                list.add(value);
            }
        }
        System.out.println(sql);
        System.out.println(list);
        SqlAndParam sp = new SqlAndParam();
        sp.setParams(list);
        sp.setSql(sql);
        return sp;
    }
}
