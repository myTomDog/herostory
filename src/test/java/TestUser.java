import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试用户
 */
public class TestUser {
    /**
     * 当前血量
     */
    public int currHp;

    /**
     * 当前血量, 使用 AtomicInteger 确实可以保证线程安全.
     * 但是, 用户类中有那么多字段不能全用 Atomic 类型,
     * 这样会让用户类变得特别臃肿...
     * 而且, 这样做也还是不能彻底解决问题, 例如:
     * 属性 A 本身是线程安全的,
     * 属性 B 本身也是线程安全的,
     * 但我们无法保证同时操作 A 和 B 是线程安全的!
     */
    public AtomicInteger safeCurrHp;

    /**
     * 减血
     *
     * @param val
     */
    synchronized public void subtractHp(int val) {
        if (val <= 0) {
            return;
        }

        this.currHp = this.currHp - val;
    }

    /**
     * 攻击目标用户
     *
     * @param targetUser 目标用户
     */
    /*synchronized */public void attkUser(TestUser targetUser) {
        if (null == targetUser) {
            return;
        }

        int attkDmg;

        synchronized (this) {
            // 在这里计算我的攻击伤害,
            // 注意只有降低锁的粒度才能避免死锁的产生...
            // 可是这样做的话,
            // 就和游戏的业务逻辑纠缠在一起了!
            // 游戏业务逻辑时而简单时而复杂,
            // 加锁粒度时而调高时而调低,
            // 开发难度实在是太大了...
            // 最后在线上运行的时候, 我们往往分不清:
            // -- 是业务逻辑调用错误导致的死锁?
            // -- 还是死锁导致的业务逻辑错误?
            attkDmg = 10;
        }

        targetUser.subtractHp(attkDmg);
    }
}
