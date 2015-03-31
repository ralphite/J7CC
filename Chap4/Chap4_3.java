package J7CC.Chap4;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/30/2015.
 */
public class Chap4_3 {

    public static class Task implements Runnable {
        private Date initDate;
        private String name;

        public Task(String name) {
            this.initDate = new Date();
            this.name = name;
        }

        @Override
        public void run() {
            System.out.printf("%s: Task %s: Created on: %s\n",
                    Thread.currentThread().getName(), name, initDate);
            System.out.printf("%s: Task %s: Started on: %s\n",
                    Thread.currentThread().getName(), name, new Date());

            try {
                Long duration = (long) (Math.random() * 10);
                System.out.printf("%s: Task %s: Doing a task during %d seconds.\n",
                        Thread.currentThread().getName(), name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("%s: Task %s: Finished on: %s\n",
                    Thread.currentThread().getName(), name, new Date());
        }
    }

    public static class Server {
        private ThreadPoolExecutor executor;

        public Server() {
            this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        }

        public void executeTask(Task task) {
            System.out.printf("Server: A new task has arrived.\n");
            executor.execute(task);
            System.out.printf("Server: Poll size: %d\n",
                    executor.getPoolSize());
            System.out.printf("Server: Active count: %d\n",
                    executor.getActiveCount());
            System.out.printf("Server: Completed tasks: %d\n",
                    executor.getCompletedTaskCount());
            System.out.printf("Server: Task count: %d\n",
                    executor.getTaskCount());
        }

        public void endServer() {
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();

        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task " + i);
            server.executeTask(task);
        }

        server.endServer();
    }
}
