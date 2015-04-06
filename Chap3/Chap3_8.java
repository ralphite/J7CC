package Chap3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Created by yawen on 3/30/2015.
 */
public class Chap3_8 {
    public static class Producer implements Runnable {
        private List<String> buffer;
        private final Exchanger<List<String>> exchanger;

        public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.exchanger = exchanger;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            int cycle = 1;
            for (int i = 0; i < 10; i++) {
                System.out.printf("Producer: Cycle %d\n", cycle);
                for (int j = 0; j < 10; j++) {
                    String message = "Event " + (i * 10 + j);
                    System.out.printf("Producer: %s\n", message);
                    buffer.add(message);
                }
                try {
                    buffer = exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Producer: " + buffer.size());
                cycle++;
            }
        }
    }

    public static class Consumer implements Runnable {
        private List<String> buffer;
        private final Exchanger<List<String>> exchanger;

        public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            int cycle = 1;

            for (int i = 0; i < 10; i++) {
                System.out.printf("Consumer: Cycle %d\n", cycle);

                try {
                    buffer = exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Consumer: " + buffer.size());
                for (int j = 0; j < 10; j++) {
                    String message = buffer.get(0);
                    System.out.println("Consumer: " + message);
                    buffer.remove(0);
                }

                cycle++;
            }
        }
    }

    public static void main(String[] args) {
        List<String> buffer1 = new ArrayList<>();
        List<String> buffer2 = new ArrayList<>();

        Exchanger<List<String>> exchanger = new Exchanger<>();

        Producer producer = new Producer(buffer1, exchanger);
        Consumer consumer = new Consumer(buffer2, exchanger);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }
}
