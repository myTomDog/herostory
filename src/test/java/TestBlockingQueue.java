import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 测试阻塞队列
 */
public class TestBlockingQueue {
    /**
     * 测试入口函数
     *
     * @param argvArray 命令行参数数组
     */
    static public void main(String[] argvArray) {
        (new TestBlockingQueue()).test2();
    }

    /**
     * 测试 1, 测试阻塞队列
     */
    private void test1() {
        // 阻塞队列
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>();

        // 第一个线程往队列里加数
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // 往队列里加数
                blockingQueue.offer(i);
            }
        });

        // 第二个线程往队列里加数
        Thread thread2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // 往队列列加数
                blockingQueue.offer(i);
            }
        });

        // 第三个线程从队列里取数
        Thread thread3 = new Thread(() -> {
            try {
                while (true) {
                    // 从队列里取数, 并打印
                    Integer val = blockingQueue.take();
                    System.out.println("获取数值 = " + val);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 测试 1, 测试非阻塞队列
     */
    private void test2() {
        MyExecutorService es = new MyExecutorService();

        // 第一个线程往队列里加数
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                final int currVal = i;
                es.submit(() -> {
                    System.out.println("i = " + currVal);
                });
            }
        });

        // 第二个线程往队列里加数
        Thread thread2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                final int currVal = i;
                es.submit(() -> {
                    System.out.println("i = " + currVal);
                });
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 模拟 ExecutorService
     */
    class MyExecutorService {
        /**
         * 阻塞队列, 阻塞队列中塞入的是 Runnable 接口
         */
        private final BlockingQueue<Runnable> _blockingQueue = new LinkedBlockingQueue<>();

        /**
         * 内置线程
         */
        private final Thread _thread;

        /**
         * 类默认构造器
         */
        MyExecutorService() {
            // 创建线程
            this._thread = new Thread(() -> {
                try {
                    while (true) {
                        // 从队列里取出 Runnable
                        Runnable r = this._blockingQueue.take();
                        if (null != r) {
                            r.run();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            this._thread.start();
        }

        /**
         * 提交一个 Runnable
         *
         * @param r Runnable
         */
        public void submit(Runnable r) {
            if (null != r) {
                this._blockingQueue.offer(r);
            }
        }
    }
}
