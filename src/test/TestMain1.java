package test;

import dao.CarDao;
import dao.CarDao2;
import domain.Car;
import jdbc.JdbcFactory;
import util.MyFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TestMain1 {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Car car = new Car(null, "absabsabs", "red", 30000);
//        CarDao2 dao = new CarDao2();
        CarDao2 dao = MyFactory.getFactory().getSqlSession().createDaoImp(CarDao2.class);
//        dao.insert(car);
//        int num = dao.delete(5);
        for (int i=0; i<200; i++) {
            List<Car> list = dao.select();
            System.out.println(i+1+"---------------------");
            for (Car c : list) {
                System.out.println(c);
            }
        }
//        System.out.println(num);
//        int num = dao.delete(6);
//        System.out.println(num);
//        List<Car> list = dao.save5(car);
////        System.out.println(num);
//        List<Car> list = dao.select();
////        dao.save3();
////        List<Car> list = dao.save3();
//////        List<Car> list = dao.save(car);
//        JdbcFactory j = new JdbcFactory();

    }
}
