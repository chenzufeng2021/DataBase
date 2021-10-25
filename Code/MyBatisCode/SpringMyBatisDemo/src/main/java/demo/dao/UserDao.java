package demo.dao;

import demo.entity.User;

import java.util.List;

/**
 * 接口中的方法与SQL映射文件一致
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
