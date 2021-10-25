package demo;

import demo.dao.UserDao;
import demo.entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyBatisTest {
    @Test
    public void test() {
        ApplicationContext context = new ClassPathXmlApplicationContext("demo/user.xml");
        UserDao bean = (UserDao) context.getBean("userDao");
        bean.addUser(new User(3, "chenzf", "123"));
        bean.selectAllUser().forEach(System.out::println);
    }
}