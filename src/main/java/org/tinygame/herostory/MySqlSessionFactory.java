package org.tinygame.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * MySql 会话工厂
 */
public final class MySqlSessionFactory {
    /**
     * MyBatis Sql 会话工厂
     */
    static private SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化类默认构造器
     */
    private MySqlSessionFactory() {
    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                Resources.getResourceAsStream("MyBatisConfig.xml")
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 开启 MySql 会话
     *
     * @return MySql 会话
     */
    static public SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }
}
