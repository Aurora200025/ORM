package jdbc.annocations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)     //可以用在方法上
@Retention(RetentionPolicy.RUNTIME)     //在jvm中存在 可以通过反射注解获得信息
@Inherited  //注解可以继承
public @interface Update {

    public String value();
}
