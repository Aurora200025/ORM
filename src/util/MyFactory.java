package util;

import jdbc.JdbcFactory;

public class MyFactory {

    private static JdbcFactory factory;

    static {
        factory = new JdbcFactory("mysql.properties");
    }
    public static JdbcFactory getFactory() {
//        System.out.println(factory);
        return factory;
    }
}
