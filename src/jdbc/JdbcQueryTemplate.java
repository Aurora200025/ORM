package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcQueryTemplate extends JdbcTemplate {
    public JdbcQueryTemplate(String className, String url, String username, String password) {
        super(className, url, username, password);
    }

    @Override
    protected List<Map<String, Object>> Five() throws SQLException {
        //在rs.close之前先把值存在list的map集合里返回
        ResultSet rs = preparedStatement.executeQuery();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        while (rs.next()) {//将键值对存入一个map集合 再将多个map集合存入list集合
            Map<String, Object> row = new HashMap<>();
            for (int i=1; i<=rs.getMetaData().getColumnCount(); i++) {
                String key = rs.getMetaData().getColumnName(i);
                Object value = rs.getObject(i);
                row.put(key.toLowerCase(), value);
            }
            rows.add(row);
        }
        return rows;
    }
}
