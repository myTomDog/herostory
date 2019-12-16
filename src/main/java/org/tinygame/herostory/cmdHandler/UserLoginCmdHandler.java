package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户登陆指令处理器
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

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

        // 用户登陆
        UserEntity userEntity = null;

        try {
            userEntity = LoginService.getInstance().userLogin(
                userName, password
            );
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return;
        }

        if (null == userEntity) {
            LOGGER.error("用户登陆失败, userName = {}", cmd.getUserName());
            return;
        }

        LOGGER.info(
            "用户登陆成功, userId = {}, userName = {}",
            userEntity.userId,
            userEntity.userName
        );

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
    }
}
