package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

/**
 * 登陆服务
 */
public class LoginService {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    /**
     * 单例对象
     */
    static private final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆
     *
     * @param userName 用户名称
     * @param password 密码
     * @return 用户实体
     */
    public UserEntity userLogin(String userName, String password) {
        if (null == userName ||
            null == password) {
            return null;
        }

        try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
            // 获取 DAO 对象,
            // 注意: 这个 IUserDao 接口咱们是没有具体实现的,
            // 但如果你听过前面的课,
            // 你可能会猜到这里面究竟发生了什么... :)
            IUserDao dao = mySqlSession.getMapper(IUserDao.class);

            // 看看当前线程
            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

            // 更间用户名称获取用户实体
            UserEntity userEntity = dao.getUserByName(userName);

            if (null != userEntity) {
                // 判断用户密码
                if (!password.equals(userEntity.password)) {
                    // 用户密码错误,
                    LOGGER.error(
                        "用户密码错误, userId = {}, userName = {}",
                        userEntity.userId,
                        userName
                    );

                    throw new RuntimeException("用户密码错误");
                }
            } else {
                // 如果用户实体为空, 则新建用户!
                userEntity = new UserEntity();
                userEntity.userName = userName;
                userEntity.password = password;
                userEntity.heroAvatar = "Hero_Shaman"; // 默认使用萨满

                // 将用户实体添加到数据库
                dao.insertInto(userEntity);
            }

            return userEntity;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }
}
