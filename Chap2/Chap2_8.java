package Chap2;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yawen on 3/28/2015.
 */
public class Chap2_8 {
    public static class FileMock {
        private String[] content;
        private int index;

        public FileMock(int size, int length) {
            content = new String[size];
            for (int i = 0; i < size; i++) {
                StringBuilder builder = new StringBuilder(length);
                for (int j = 0; j < length; j++) {
                    int indice = (int) (Math.random() * 255);
                    builder.append((char) indice);
                }
                content[i] = builder.toString();
            }
            index = 0;
        }

        public boolean hasMoreLines() {
            return index < content.length;
        }

        public String getLine() {
            if (this.hasMoreLines()) {
                System.out.println("Mock: " + (content.length - index));
                return content[index++];
            }
            return null;
        }
    }

    public static class Buffer {
        private LinkedList<String> buffer;
        private int maxSize;
        private ReentrantLock lock;
        private Condition lines;
        private Condition space;
        private boolean pendingLines;

        public Buffer(int maxSize) {
            this.maxSize = maxSize;
            buffer = new LinkedList<>();
            lock = new ReentrantLock();
            lines = lock.newCondition();
            space = lock.newCondition();
            pendingLines = true;
        }

        public void insert(String line) {
            lock.lock();
            try {
                while (buffer.size() == maxSize) {
                    space.await();
                }
                buffer.offer(line);
                System.out.printf("%s: Inserted Line: %d\n",
                        Thread.currentThread().getName(), buffer.size());
                lines.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public String get() {
            String line = null;
            lock.lock();
            try {
                while ((buffer.size() == 0) && (hasPendingLines())) {
                    lines.await();
                }
                if (hasPendingLines()) {
                    line = buffer.poll();
                    System.out.printf("%s: Line Read: %d\n", Thread.currentThread().getName(),
                            buffer.size());
                    space.signalAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            return line;
        }

        public void setPendingLines(boolean pendingLines) {
            this.pendingLines = pendingLines;
        }

        public boolean hasPendingLines() {
            return pendingLines || buffer.size() > 0;
        }
    }

    public static class Producer implements Runnable {
        private FileMock mock;
        private Buffer buffer;

        public Producer(FileMock mock, Buffer buffer) {
            this.mock = mock;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            buffer.setPendingLines(true);
            while (mock.hasMoreLines()) {
                String line = mock.getLine();
                buffer.insert(line);
            }
            buffer.setPendingLines(false);
        }
    }

    public static class Consumer implements Runnable {
        private Buffer buffer;

        public Consumer(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (buffer.hasPendingLines()) {
                String line = buffer.get();
                processLine(line);
            }
        }

        private void processLine(String line) {
            try {
                Random random = new Random();
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FileMock mock = new FileMock(100, 10);
        Buffer buffer = new Buffer(20);

        Producer producer = new Producer(mock, buffer);

        Thread producerThread = new Thread(producer, "Producer");

        Consumer[] consumers = new Consumer[3];
        Thread[] consumerThreads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            consumers[i] = new Consumer(buffer);
            consumerThreads[i] = new Thread(consumers[i], "Consumer " + i);
        }

        producerThread.start();
        for (int i = 0; i < 3; i++) {
            consumerThreads[i].start();
        }
    }
}
