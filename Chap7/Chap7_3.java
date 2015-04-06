package Chap7;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ralph on 4/6/15 3:28 PM 3:28 PM.
 */
public class Chap7_3 {
    public static class MyPriorityTask implements Runnable, Comparable<MyPriorityTask> {
        private int priority;
        private String name;

        public MyPriorityTask(String name, int priority) {
            this.priority = priority;
            this.name = name;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public int compareTo(MyPriorityTask o) {
            if (this.getPriority() < o.getPriority()) {
                return 1;
            } else if (this.getPriority() > o.getPriority()) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        public void run() {
            System.out.printf("MyPriorityTask: %s Priority : %d\n", name, priority);

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>());

        for (int i = 0; i < 4; i++) {
            MyPriorityTask task = new MyPriorityTask("Task " + i, i);
            executor.execute(task);
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 4; i < 8; i++) {
            MyPriorityTask task = new MyPriorityTask("Task " + i, i);
            executor.execute(task);
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: End of the program.\n");
    }
}
