package demo;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 测试类
 * 首先使用输入流读取配置文件，然后根据配置信息构建SqlSessionFactory对象。
 * 接下来通过SqlSessionFactory对象创建SqlSession对象，并使用SqlSession对象的方法执行数据库操作。
 * @author chenzufeng
 */
public class MyBatisTest {
    public static void main(String[] args) {
        try {
            // 读取配置文件 mybatis-config.xml
            InputStream config = Resources.getResourceAsStream("mybatis_config.xml");

            // 根据配置文件构建SqlSessionFactory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);

            // 通过 SqlSessionFactory创建SqlSession
            SqlSession sqlSession = sqlSessionFactory.openSession();

             //通过SqlSession获取UserDao对象
            UserDao userDao = sqlSession.getMapper(UserDao.class);

            /// SqlSession执行映射文件中定义的SQL，并返回映射结果
            /// User user1= sqlSession.selectOne("UserMapper.selectUserById", 1);


            // 添加一个用户
            User user = new User();
            user.setName("chen");
            user.setPassword("123");
            userDao.addUser(user);
            /// sqlSession.insert("UserMapper.addUser", user);

            // 查询一个用户
            /// User user1= sqlSession.selectOne("UserMapper.selectUserById", 1);
            User user1 = userDao.selectUserById(1);
            System.out.println(user1);

            // 修改一个用户
            User user2 = new User();
            user2.setId(1);
            user2.setName("zufeng");
            user2.setPassword("123456");
            userDao.updateUser(user2);
            /// sqlSession.update("UserMapper.updateUser", user2);

            // 查询所有用户
            userDao.selectAllUser().forEach(System.out::println);
            ///List<User> users = sqlSession.selectList("UserMapper.selectAllUser");

            // 提交事务
            sqlSession.commit();
            // 关闭sqlSession
            sqlSession.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
