package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户管理器
 */
public final class UserManager {
    /**
     * 用户字典
     */
    static private final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    /**
     * 私有化类默认构造器
     */
    private UserManager() {
    }

    /**
     * 添加用户
     *
     * @param newUser 新用户
     */
    static public void addUser(User newUser) {
        if (null != newUser) {
            _userMap.putIfAbsent(newUser.userId, newUser);
        }
    }

    /**
     * 根据用户 Id 移除用户
     *
     * @param userId 用户 Id
     */
    static public void removeUserById(int userId) {
        _userMap.remove(userId);
    }

    /**
     * 列表用户
     *
     * @return 用户列表
     */
    static public Collection<User> listUser() {
        return _userMap.values();
    }

    /**
     * 根据 Id 获取用户
     *
     * @param userId 用户 Id
     * @return 用户对象
     */
    static public User getUserById(int userId) {
        return _userMap.get(userId);
    }
}
