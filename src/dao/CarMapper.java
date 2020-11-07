package dao;

import domain.Car;
import jdbc.RowMapper;

import java.sql.SQLException;
import java.util.Map;

public class CarMapper implements RowMapper<Car> {
    @Override
    public Car mapping(Map<String, Object> rs) throws SQLException {
        Integer cno = (Integer) rs.get("cno");
        String cname = (String) rs.get("cname");
        String color = (String) rs.get("color");
        Integer price = (Integer) rs.get("price");
        return new Car(cno, cname, color, price);
    }
}
