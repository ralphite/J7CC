package J7CC.Chap4;

import java.util.concurrent.*;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap4_9 {

    public static class Task implements Callable<String> {

        @Override
        public String call() throws Exception {
            while (true) {
                System.out.println("Task: Test");
                Thread.sleep(100);
            }
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newCachedThreadPool();

        Task task = new Task();

        System.out.printf("Main: Executing the task\n");
        Future<String> stringFuture = executor.submit(task);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Canceling the task\n");
        stringFuture.cancel(true);

        System.out.printf("Main: Cancelled: %s\n", stringFuture.isCancelled());
        System.out.printf("Main: Done: %s\n", stringFuture.isDone());

        executor.shutdown();
        System.out.printf("Main: The executor has finished.\n");
    }
}
