package org.tinygame.herostory.model;

/**
 * 用户
 *
 * @author Administrator
 * @date 2019/12/6
 */
public class User {

    /**
     * 用户 Id
     */
    public int userId;

    /**
     * 英雄形象
     */
    public String heroAvatar;

    /**
     * 当前血量
     */
    public int currHp;

    /**
     * 移动状态
     */
    public final MoveState moveState = new MoveState();
}
