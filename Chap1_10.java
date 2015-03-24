package J7CC;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_10 {
    public static class UnsafeTask implements Runnable {

        private Date startDate;

        @Override
        public void run() {
            startDate = new Date();
            System.out.printf("Starting Thread: %s : %s\n",
                    Thread.currentThread().getId(), startDate);

            try {
                TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread finished: %s : %s\n",
                    Thread.currentThread().getId(), startDate);
        }
    }

    public static class SafeTask implements Runnable {

        private static ThreadLocal<Date> startDate = new ThreadLocal<Date>(){
            protected Date initialValue() {
                return new Date();
            }
        };

        @Override
        public void run() {
            System.out.printf("Starting Thread: %s : %s\n",
                    Thread.currentThread().getId(), startDate.get());

            try {
                TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread finished: %s : %s\n",
                    Thread.currentThread().getId(), startDate.get());
        }
    }

    public static void main(String[] args) {
//        UnsafeTask unsafeTask = new UnsafeTask();
//        for(int i = 0; i < 3; i++) {
//            Thread thread = new Thread(unsafeTask);
//            thread.start();
//
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        SafeTask safeTask = new SafeTask();
        for(int i = 0; i < 3; i++) {
            Thread thread = new Thread(safeTask);
            thread.start();

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
