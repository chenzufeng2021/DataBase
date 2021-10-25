package demo;

import java.util.List;

/**
 * @author chenzufeng
 */
public interface UserDao {
    /**
     * 接口方法对应的SQL映射文件UserMapper.xml中的id
     */
    public User selectUserById(Integer id);

    public List<User> selectAllUser();

    public void addUser(User user);

    public void updateUser(User user);

    public void deleteUser(User user);
}
