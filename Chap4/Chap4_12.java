package Chap4;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap4_12 {
    public static class RejectedTaskController implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.printf(">>>RejectedTaskController: The task %s has been rejected.\n",
                    r.toString());
            System.out.printf(">>>RejectedTaskController: %s\n",
                    executor.toString());
            System.out.printf(">>>RejectedTaskController: Terminating: %s\n",
                    executor.isTerminating());
            System.out.printf(">>>RejectedTaskController: Terminated: %s\n",
                    executor.isTerminated());
        }
    }

    public static class Task implements Runnable {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println("Task " + name + ": Starting");

            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("Task %s: ReportGenerator: Generating a report during" +
                        " %d seconds.\n", name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Task %s: Ending\n", name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        RejectedTaskController controller = new RejectedTaskController();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        executor.setRejectedExecutionHandler(controller);

        System.out.printf("Main: Starting.\n");
        for (int i = 0; i < 3; i++) {
            Task task = new Task("Task " + i);
            executor.submit(task);
        }

        System.out.printf("Main: Shutting down the executor.\n");
        executor.shutdown();

        System.out.printf("Main: Sending another task.\n");
        Task task = new Task("RejectedTask");
        executor.submit(task);

        System.out.print("Main: End\n");
    }
}
