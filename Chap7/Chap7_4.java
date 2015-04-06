package Chap7;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by ralph on 4/6/15 4:09 PM.
 */
public class Chap7_4 {
    public static class MyThread extends Thread {
        private Date createDate;
        private Date startDate;
        private Date finishDate;

        public MyThread(Runnable target, String name) {
            super(target, name);
            setCreateDate();
        }

        @Override
        public void run() {
            setStartDate();
            super.run();
            setFinishDate();
        }

        public void setCreateDate() {
            createDate = new Date();
        }

        public void setStartDate() {
            startDate = new Date();
        }

        public void setFinishDate() {
            finishDate = new Date();
        }

        public long getExecutionTime() {
            return finishDate.getTime() - startDate.getTime();
        }

        @Override
        public String toString() {
            return getName() + ": Create Date: " + createDate + ", Running Time: " +
                    getExecutionTime() + " Milliseconds.";
        }
    }

    public static class MyThreadFactory implements ThreadFactory {
        private int counter;
        private String prefix;

        public MyThreadFactory(String prefix) {
            this.prefix = prefix;
            counter = 1;
        }

        @Override
        public Thread newThread(Runnable r) {
            MyThread myThread = new MyThread(r, prefix + "-" + counter);
            counter++;
            return myThread;
        }
    }

    public static class MyTask implements Runnable {
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MyThreadFactory myThreadFactory = new MyThreadFactory("MyThreadFactory");
        MyTask task = new MyTask();
        Thread thread = myThreadFactory.newThread(task);

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Thread information.\n");
        System.out.printf("%s\n", thread);
        System.out.printf("Main: End of program.\n");
    }
}