import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试多线程
 */
public class TestMultiThread {
    /**
     * 测试入口函数
     *
     * @param argvArray 命令行参数数组
     */
    static public void main(String[] argvArray) {
        for (int i = 1; i <= 10000; i++) {
            System.out.println("第 " + i + " 次测试");
            (new TestMultiThread()).test4();
        }
    }

    /**
     * 第一个测试
     */
    private void test1() {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;

        Thread[] threadArray = new Thread[2];

        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i] = new Thread(() -> {
                newUser.currHp = newUser.currHp - 10;
            });
        }

        for (Thread currThread : threadArray) {
            currThread.start();
        }

        try {
            for (Thread currThread : threadArray) {
                currThread.join();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (newUser.currHp != 80) {
            throw new RuntimeException(
                "当前血量错误, currHp = " + newUser.currHp
            );
        }

        System.out.println(
            "当前血量正确, currHp = " + newUser.currHp
        );
    }

    /**
     * 第二个测试, 使用 synchronized
     */
    private void test2() {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;

        Thread[] threadArray = new Thread[2];

        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i] = new Thread(() -> {
                newUser.subtractHp(10);
            });
        }

        for (Thread currThread : threadArray) {
            currThread.start();
        }

        try {
            for (Thread currThread : threadArray) {
                currThread.join();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (newUser.currHp != 80) {
            throw new RuntimeException(
                "当前血量错误, currHp = " + newUser.currHp
            );
        }

        System.out.println(
            "当前血量正确, currHp = " + newUser.currHp
        );
    }

    /**
     * 第三个测试, 死锁
     */
    private void test3() {
        TestUser newUser1 = new TestUser();
        newUser1.currHp = 100;
        TestUser newUser2 = new TestUser();
        newUser2.currHp = 100;

        Thread[] threadArray = new Thread[2];
        threadArray[0] = new Thread(() -> {
            newUser1.attkUser(newUser2);
        });
        threadArray[1] = new Thread(() -> {
            newUser2.attkUser(newUser1);
        });

        for (Thread currThread : threadArray) {
            currThread.start();
        }

        try {
            for (Thread currThread : threadArray) {
                currThread.join();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("攻击完成");
    }

    /**
     * 第四个测试, 使用 AtomicInteger
     */
    private void test4() {
        TestUser newUser = new TestUser();
        newUser.safeCurrHp = new AtomicInteger(100);

        Thread[] threadArray = new Thread[2];

        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i] = new Thread(() -> {
                newUser.safeCurrHp.addAndGet(-10);
            });
        }

        for (Thread currThread : threadArray) {
            currThread.start();
        }

        try {
            for (Thread currThread : threadArray) {
                currThread.join();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (newUser.safeCurrHp.get() != 80) {
            throw new RuntimeException(
                "当前血量错误, currHp = " + newUser.safeCurrHp.get()
            );
        }

        System.out.println(
            "当前血量正确, currHp = " + newUser.safeCurrHp.get()
        );
    }
}
