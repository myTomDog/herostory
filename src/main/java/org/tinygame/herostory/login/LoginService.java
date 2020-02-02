package org.tinygame.herostory.login;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

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
     * @param callback 回调函数
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName ||
            null == password) {
            return;
        }

        // 创建异步操纵
        AsyncGetUserByName asyncOp = new AsyncGetUserByName(userName, password) {
            @Override
            public void doFinish() {
                if (null != callback) {
                    // 执行回调函数
                    callback.apply(this.getUserEntity());
                }
            }
        };

        // 执行异步操纵
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    /**
     * 更新 Redis 中的用户基本信息
     *
     * @param userEntity 用户实体
     */
    private void updateUserBasicInfoInRedis(UserEntity userEntity) {
        if (null == userEntity ||
            userEntity.userId <= 0) {
            return;
        }

        try (Jedis redis = RedisUtil.getJedis()) {
            // 获取用户 Id
            int userId = userEntity.userId;

            // 创建 JSON 对象
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userId", userId);
            jsonObj.put("userName", userEntity.userName);
            jsonObj.put("heroAvatar", userEntity.heroAvatar);

            // 更新 Redis 数据
            redis.hset("User_" + userId, "BasicInfo", jsonObj.toJSONString());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 异步方式获取用户
     */
    private class AsyncGetUserByName implements IAsyncOperation {
        /**
         * 用户名称
         */
        private final String _userName;

        /**
         * 密码
         */
        private final String _password;

        /**
         * 用户实体
         */
        private UserEntity _userEntity = null;

        /**
         * 类参数构造器
         *
         * @param userName 用户名称
         * @param password 密码
         * @throws IllegalArgumentException if null == userName || null == password
         */
        AsyncGetUserByName(String userName, String password) {
            if (null == userName ||
                null == password) {
                throw new IllegalArgumentException();
            }

            _userName = userName;
            _password = password;
        }

        /**
         * 获取用户实体
         *
         * @return 用户实体
         */
        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public int getBindId() {
            return _userName.charAt(_userName.length() - 1);
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                // 获取 DAO 对象,
                // 注意: 这个 IUserDao 接口咱们是没有具体实现的,
                // 但如果你听过前面的课,
                // 你可能会猜到这里面究竟发生了什么... :)
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                // 看看当前线程
                LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                // 更间用户名称获取用户实体
                UserEntity userEntity = dao.getUserByName(_userName);

                if (null != userEntity) {
                    // 判断用户密码
                    if (!_password.equals(userEntity.password)) {
                        // 用户密码错误,
                        LOGGER.error(
                            "用户密码错误, userId = {}, userName = {}",
                            userEntity.userId,
                            _userName
                        );

                        return;
                    }
                } else {
                    // 如果用户实体为空, 则新建用户!
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman"; // 默认使用萨满

                    // 将用户实体添加到数据库
                    dao.insertInto(userEntity);
                }

                _userEntity = userEntity;

                // 更新 Redis 中的用户基本信息
                LoginService.getInstance().updateUserBasicInfoInRedis(userEntity);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
