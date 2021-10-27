package transaction;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import transaction.entity.User;
import transaction.service.UserService;

public class TransactionTest {
    @Test
    public void test() {
        ApplicationContext context = new ClassPathXmlApplicationContext("transaction/user.xml");
        UserService userService = (UserService) context.getBean("userService");
        userService.register(new User(4, "zufeng", "123456"));
        userService.getAllUser().forEach(System.out::println);
    }
}