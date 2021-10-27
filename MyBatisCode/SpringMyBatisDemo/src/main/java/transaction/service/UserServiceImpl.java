package transaction.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import transaction.dao.UserDao;
import transaction.entity.User;

import java.util.List;

/**
 * 业务接口实现类
 * 在类上添加@Transactional注解，使类中所有方法加上事务
 * @author chenzufeng
 */
@Transactional(rollbackFor = RuntimeException.class)
public class UserServiceImpl implements UserService {
    /**
     * UserServiceImpl依赖UserDao
     * 因此将其作为成员变量，并提供Getter和Setter方法
     */
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 注册用户：调用UserDao#addUser
     * 增方法：使用默认的传播属性值REQUIRED
     */
    @Override
    public void register(User user) {
        userDao.addUser(user);
        // 验证事务效果
        // throw new RuntimeException("测试事务效果！");
    }

    /**
     * 查询方法：显式指定传播属性的值为SUPPORTS
     * 只进行查询操作的业务方法，可以加入只读属性，提高运行效率
     * @return 用户列表
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public List<User> getAllUser() {
        return userDao.selectAllUser();
    }
}
