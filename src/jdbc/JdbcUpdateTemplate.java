package jdbc;

import java.sql.SQLException;

public class JdbcUpdateTemplate extends JdbcTemplate{

    public JdbcUpdateTemplate(String sql, String url, String username, String password) {
        super(sql,url,username,password);
    }

    @Override
    protected Object Five() throws SQLException {
        return preparedStatement.executeUpdate();
    }
}
