package org.tinygame.herostory.login.db;

import org.apache.ibatis.annotations.Param;

/**
 * 用户 DAO
 */
public interface IUserDao {
    /**
     * 根据用户名称获取用户实体
     *
     * @param userName 用户名称
     * @return 用户实体
     */
    UserEntity getUserByName(@Param("userName") String userName);

    /**
     * 添加用户实体
     *
     * @param newUserEntity 用户实体
     */
    void insertInto(UserEntity newUserEntity);
}
