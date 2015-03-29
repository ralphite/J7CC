package J7CC.Chap3;

import java.util.concurrent.Semaphore;

/**
 * Created by yawen on 3/29/2015.
 */
public class Chap3_2 {
    public static class PrintQueue {
        private final Semaphore semaphore;

        public PrintQueue() {
            this.semaphore = new Semaphore(1);
        }

        public void printJob(Object document) {
            try {
                semaphore.acquire();

                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: PrintQueue: Printing a job during %d seconds\n",
                        Thread.currentThread().getName(), duration);

                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }

    public static class Job implements Runnable {
        private PrintQueue printQueue;

        public Job(PrintQueue printQueue) {
            this.printQueue = printQueue;
        }

        @Override
        public void run() {
            System.out.printf("%s: Going to print a job\n",
                    Thread.currentThread().getName());

            printQueue.printJob(new Object());

            System.out.printf("%s: The document has been printed\n",
                    Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        PrintQueue printQueue = new PrintQueue();

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
    }
}
