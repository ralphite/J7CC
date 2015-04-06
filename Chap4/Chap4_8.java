package Chap4;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap4_8 {
    public static class Task implements Runnable {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.printf("%s: Starting at: %s\n",
                    name, new Date());
        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        System.out.printf("Main: Starting at: %s\n", new Date());

        Task task = new Task("Task");

        ScheduledFuture<?> scheduledFuture =
                executorService.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);

        for (int i = 0; i < 10; i++) {
            System.out.printf("Main: Delay: %d\n",
                    scheduledFuture.getDelay(TimeUnit.MILLISECONDS));

            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Finished at: %s\n", new Date());
    }
}
