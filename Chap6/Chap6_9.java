package Chap6;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap6_9 {
    public static class Incrementer implements Runnable {
        private AtomicIntegerArray vector;

        public Incrementer(AtomicIntegerArray vector) {
            this.vector = vector;
        }

        @Override
        public void run() {
            for (int i = 0; i < vector.length(); i++) {
                vector.getAndIncrement(i);
            }
        }
    }

    public static class Decrementer implements Runnable {
        private AtomicIntegerArray vector;

        public Decrementer(AtomicIntegerArray vector) {
            this.vector = vector;
        }

        @Override
        public void run() {
            for (int i = 0; i < vector.length(); i++) {
                vector.getAndDecrement(i);
            }
        }
    }

    public static void main(String[] args) {
        final int THREADS = 100;
        AtomicIntegerArray vector = new AtomicIntegerArray(1000);

        Incrementer incrementer = new Incrementer(vector);
        Decrementer decrementer = new Decrementer(vector);

        Thread[] incrementerThreads = new Thread[THREADS];
        Thread[] decrementerThreads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            incrementerThreads[i] = new Thread(incrementer);
            decrementerThreads[i] = new Thread(decrementer);

            incrementerThreads[i].start();
            decrementerThreads[i].start();
        }

        for (int i = 0; i < THREADS; i++) {
            try {
                incrementerThreads[i].join();
                decrementerThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < vector.length(); i++) {
            if (vector.get(i) != 0) {
                System.out.println("Vector[" + i + "]: " + vector.get(i));
            }
        }

        System.out.printf("Main: End of example.\n");
    }
}
