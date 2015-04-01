package J7CC.Chap6;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap6_7 {
    public static class TaskLocalRandom implements Runnable {
        public TaskLocalRandom() {
            ThreadLocalRandom.current();
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            for (int i = 0; i < 10; i++) {
                System.out.printf("%s: %d\n", name,
                        ThreadLocalRandom.current().nextInt(10));
            }
        }
    }

    public static void main(String[] args) {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            TaskLocalRandom taskLocalRandom = new TaskLocalRandom();
            threads[i] = new Thread(taskLocalRandom);
            threads[i].start();
        }
    }
}
