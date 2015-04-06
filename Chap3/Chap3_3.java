package Chap3;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yawen on 3/29/2015.
 */
public class Chap3_3 {
    public static class PrintQueue {
        private final Semaphore semaphore;
        private boolean[] freePrinters;
        private Lock printersLock;

        public PrintQueue() {
            this.semaphore = new Semaphore(3);
            this.freePrinters = new boolean[3];
            for (int i = 0; i < 3; i++) {
                freePrinters[i] = true;
            }
            printersLock = new ReentrantLock();
        }

        public void printJob(Object document) {
            try {
                semaphore.acquire();

                int assignedPrinter = getPrinter();
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: PrintQueue: Printing a job in printer %d during %d seconds\n",
                        Thread.currentThread().getName(), assignedPrinter, duration);
                TimeUnit.SECONDS.sleep(duration);

                freePrinters[assignedPrinter] = true;

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }

        private int getPrinter() {
            int ret = -1;

            try {
                printersLock.lock();

                for (int i = 0; i < freePrinters.length; i++) {
                    if (freePrinters[i]) {
                        ret = i;
                        freePrinters[i] = false;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                printersLock.unlock();
            }

            return ret;
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
