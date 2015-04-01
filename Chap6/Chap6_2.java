package J7CC.Chap6;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap6_2 {

    public static class AddTask implements Runnable {
        private ConcurrentLinkedDeque<String> list;

        public AddTask(ConcurrentLinkedDeque<String> list) {
            this.list = list;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();

            for (int i = 0; i < 10000; i++) {
                list.add(name + ": Element " + i);
            }
        }
    }

    public static class PollTask implements Runnable {
        private ConcurrentLinkedDeque<String> list;

        public PollTask(ConcurrentLinkedDeque<String> list) {
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5000; i++) {
                list.pollFirst();
                list.pollLast();
            }
        }
    }

    public static void main(String[] args) {
        ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>();
        Thread[] threads = new Thread[100];

        for (int i = 0; i < threads.length; i++) {
            AddTask task = new AddTask(list);
            threads[i] = new Thread(task);
            threads[i].start();
        }

        System.out.printf("Main: %d AddTask threads have been launched\n",
                threads.length);

        for (Thread thread1 : threads) {
            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Size of the list: %d\n", list.size());

        for (int i = 0; i < threads.length; i++) {
            PollTask task = new PollTask(list);
            threads[i] = new Thread(task);
            threads[i].start();
        }

        System.out.printf("Main: %d PollTask threads have been launched\n",
                threads.length);

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Size of the list: %d\n", list.size());
    }
}
