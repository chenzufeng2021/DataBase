package transaction.service;

import transaction.entity.User;

import java.util.List;

/**
 * 业务接口
 * @author chenzufeng
 */
public interface UserService {
    /**
     * 注册用户
     */
    public void register(User user);

    public List<User> getAllUser();
}
