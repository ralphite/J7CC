package J7CC.Chap7;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by yawen on 4/14/2015.
 */
public class Chap7_9 {
    public static class MyAbstractQueuedSynchronizer extends
            AbstractQueuedSynchronizer {
        private AtomicInteger state;

        public MyAbstractQueuedSynchronizer() {
            this.state = new AtomicInteger(0);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return state.compareAndSet(0, 1);
        }

        @Override
        protected boolean tryRelease(int arg) {
            return state.compareAndSet(1, 0);
        }
    }

    public static class MyLock implements Lock {
        private AbstractQueuedSynchronizer synchronizer;

        public MyLock() {
            synchronizer = new MyAbstractQueuedSynchronizer();
        }

        @Override
        public void lock() {
            synchronizer.acquire(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            synchronizer.acquireInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            try {
                return synchronizer.tryAcquireNanos(1, 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return synchronizer.tryAcquireNanos(1, TimeUnit.NANOSECONDS.convert(time, unit));
        }

        @Override
        public void unlock() {
            synchronizer.release(1);
        }

        @Override
        public Condition newCondition() {
            return synchronizer.new ConditionObject();
        }
    }

    public static class Task implements Runnable {
        private MyLock lock;
        private String name;

        public Task(String name, MyLock lock) {
            this.lock = lock;
            this.name = name;
        }

        @Override
        public void run() {
            lock.lock();
            System.out.printf("--->Task: %s: Take the lock\n", name);
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.printf("--->Task: %s: Free the lock\n", name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        MyLock lock = new MyLock();

        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task-" + i, lock);
            Thread thread = new Thread(task);
            thread.start();
        }

        boolean value;

        do {
            try {
                value = lock.tryLock(1, TimeUnit.SECONDS);
                if (!value) {
                    System.out.printf("Main: Trying to get the lock\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                value = false;
            }
        } while (!value);

        System.out.printf("Main: Got the lock\n");

        lock.unlock();

        System.out.printf("Main: End of program\n");
    }
}
