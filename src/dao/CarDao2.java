package dao;

import domain.Car;
import jdbc.annocations.Delete;
import jdbc.annocations.Insert;
import jdbc.annocations.Select;

import java.util.List;
import java.util.Map;

public interface CarDao2 {

    @Insert("insert into t_car(cname, color, price) values(#{cname},#{color},#{price})")
    public void insert(Car car) ;

    @Select("select * from t_car")
    public List<Car> select () ;

    @Delete("delete from t_car where cno = #{cno}")
    public int delete(int cno) ;
}
