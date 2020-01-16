package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户登陆指令处理器
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    /**
     * 用户登陆状态字典, 防止用户连点登陆按钮
     */
    static private final Map<String, Long> USER_LOGIN_STATE_MAP = new ConcurrentHashMap<>();

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx ||
            null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        LOGGER.info(
            "用户登陆, userName = {}, password = {}",
            userName,
            password
        );

        // 事先清理超时的登陆时间
        clearTimeoutLoginTime(USER_LOGIN_STATE_MAP);

        if (USER_LOGIN_STATE_MAP.containsKey(userName)) {
            // 如果正在处理登陆操作,
            return;
        }

        // 获取系统当前时间
        final long currTime = System.currentTimeMillis();
        // 设置用户登陆时间
        USER_LOGIN_STATE_MAP.putIfAbsent(
            userName, currTime
        );

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

        // 执行用户登陆
        LoginService.getInstance().userLogin(userName, password, (userEntity) -> {
            // 移除用户登陆状态
            USER_LOGIN_STATE_MAP.remove(userName);

            if (null == userEntity) {
                LOGGER.error("用户登陆失败, userName = {}", cmd.getUserName());
                return null;
            }

            LOGGER.info(
                "用户登陆成功, userId = {}, userName = {}",
                userEntity.userId,
                userEntity.userName
            );

            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

            // 新建用户,
            User newUser = new User();
            newUser.userId = userEntity.userId;
            newUser.userName = userEntity.userName;
            newUser.heroAvatar = userEntity.heroAvatar;
            newUser.currHp = 100;
            // 并将用户加入管理器
            UserManager.addUser(newUser);

            // 将用户 Id 附着到 Channel
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.userId);

            // 登陆结果构建者
            GameMsgProtocol.UserLoginResult.Builder
                resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
            resultBuilder.setUserId(newUser.userId);
            resultBuilder.setUserName(newUser.userName);
            resultBuilder.setHeroAvatar(newUser.heroAvatar);

            // 构建结果并发送
            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);

            return null;
        });
    }

    /**
     * 清理超时的登陆时间
     *
     * @param userLoginTimeMap 用户登陆时间字典
     */
    static private void clearTimeoutLoginTime(Map<String, Long> userLoginTimeMap) {
        if (null == userLoginTimeMap ||
            userLoginTimeMap.isEmpty()) {
            return;
        }

        // 获取系统时间
        final long currTime = System.currentTimeMillis();
        // 获取迭代器
        Iterator<String> it = userLoginTimeMap.keySet().iterator();

        while (it.hasNext()) {
            // 根据用户名称获取登陆时间
            String userName = it.next();
            Long loginTime = userLoginTimeMap.get(userName);

            if (null == loginTime ||
                currTime - loginTime > 5000) {
                // 如果已经超时,
                it.remove();
            }
        }
    }
}
