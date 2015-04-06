package Chap2;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yawen on 3/28/2015.
 */
public class Chap2_4 {
    public static class EventStorage {
        private int maxSize;
        private List<Date> storage;

        public EventStorage() {
            maxSize = 10;
            storage = new LinkedList<>();
        }

        public synchronized void set() {
            while (storage.size() == maxSize) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            storage.add(new Date());
            System.out.printf("Set: %d\n", storage.size());
            notifyAll();
        }

        public synchronized void get() {
            while (storage.size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("Get: %d: %s\n", storage.size(), ((LinkedList<?>) storage).poll());
            notifyAll();
        }
    }

    public static class Producer implements Runnable {

        public Producer(EventStorage eventStorage) {
            this.eventStorage = eventStorage;
        }

        private EventStorage eventStorage;

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                eventStorage.set();
            }
        }
    }

    public static class Consumer implements Runnable {
        private EventStorage eventStorage;

        public Consumer(EventStorage eventStorage) {
            this.eventStorage = eventStorage;
        }

        @Override
        public void run() {
            for( int i = 0; i < 100; i++) {
                eventStorage.get();
            }
        }
    }

    public static void main(String[] args) {
        EventStorage eventStorage = new EventStorage();

        Producer producer = new Producer(eventStorage);
        Thread thread1 = new Thread(producer);

        Consumer consumer = new Consumer(eventStorage);
        Thread thread2 = new Thread(consumer);

        thread1.start();
        thread2.start();
    }
}
