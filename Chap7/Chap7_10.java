package J7CC.Chap7;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yawen on 4/14/2015.
 */
public class Chap7_10 {
    public static class MyPriorityTransferQueue<E> extends
            PriorityBlockingQueue<E> implements TransferQueue<E> {
        private AtomicInteger counter;
        private LinkedBlockingQueue<E> transferred;

        private ReentrantLock lock;

        public MyPriorityTransferQueue() {
            counter = new AtomicInteger(0);
            lock = new ReentrantLock();
            transferred = new LinkedBlockingQueue<>();
        }

        @Override
        public boolean tryTransfer(E e) {
            lock.lock();
            boolean value;
            if (counter.get() == 0) {
                value = false;
            } else {
                put(e);
                value = true;
            }
            lock.unlock();
            return value;
        }

        @Override
        public void transfer(E e) throws InterruptedException {
            lock.lock();
            if (counter.get() != 0) {
                put(e);
                lock.unlock();
            } else {
                transferred.add(e);
                lock.unlock();
                synchronized (e) {
                    e.wait();
                }
            }
        }

        @Override
        public boolean tryTransfer(E e, long timeout, TimeUnit unit)
                throws InterruptedException {
            lock.lock();
            if (counter.get() != 0) {
                put(e);
                lock.unlock();
                return true;
            } else {
                transferred.add(e);
                long newTimeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
                lock.unlock();

                e.wait(newTimeout);

                lock.lock();
                if (transferred.contains(e)) {
                    transferred.remove(e);
                    lock.unlock();
                    return false;
                } else {
                    lock.unlock();
                    return true;
                }
            }
        }

        @Override
        public boolean hasWaitingConsumer() {
            return counter.get() != 0;
        }

        @Override
        public int getWaitingConsumerCount() {
            return counter.get();
        }

        @Override
        public E take() throws InterruptedException {
            lock.lock();
            counter.incrementAndGet();

            E value = transferred.poll();
            if (value == null) {
                lock.unlock();
                value = super.take();
                lock.lock();
            } else {
                synchronized (value) {
                    value.notify();
                }
            }

            counter.decrementAndGet();
            lock.unlock();
            return value;
        }
    }

    public static class Event implements Comparable<Event> {
        private String thread;
        private int priority;

        public String getThread() {
            return thread;
        }

        public int getPriority() {
            return priority;
        }

        public Event(String thread, int priority) {
            this.thread = thread;
            this.priority = priority;

        }

        @Override
        public int compareTo(Event o) {
            if (this.priority > o.getPriority()) {
                return -1;
            } else if (this.getPriority() > o.getPriority()) {
                return 1;
            } else
                return 0;
        }
    }

    public static class Producer implements Runnable {
        private MyPriorityTransferQueue<Event> buffer;

        public Producer(MyPriorityTransferQueue<Event> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                Event event = new Event(Thread.currentThread().getName(), i);
                buffer.put(event);
            }
        }
    }

    public static class Consumer implements Runnable {
        private MyPriorityTransferQueue<Event> buffer;

        public Consumer(MyPriorityTransferQueue<Event> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            for (int i = 0; i < 102; i++) {
                try {
                    Event value = buffer.take();
                    System.out.printf("Consumer: %s: %d\n",
                            value.getThread(), value.getPriority());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyPriorityTransferQueue<Event> buffer = new MyPriorityTransferQueue<>();

        Producer producer = new Producer(buffer);

        Thread[] producerThreads = new Thread[10];

        for (int i = 0; i < producerThreads.length; i++) {
            producerThreads[i] = new Thread(producer);
            producerThreads[i].start();
        }

        Consumer consumer = new Consumer(buffer);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        System.out.printf("Main: Buffer: Consumer count: %d\n",
                buffer.getWaitingConsumerCount());

        Event event = new Event("Core Event", 0);
        buffer.transfer(event);
        System.out.printf("Main: Event has been transferred.\n");

        for (Thread thread : producerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TimeUnit.SECONDS.sleep(1);

        System.out.printf("Main: Buffer: Consumer count: %d\n",
                buffer.getWaitingConsumerCount());

        event = new Event("Core Event 2", 0);
        buffer.transfer(event);

        consumerThread.join();

        System.out.printf("Main: End of program.\n");
    }
}
