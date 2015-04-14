package J7CC.Chap7;

import java.util.concurrent.*;

/**
 * Created by yawen on 4/13/2015.
 */
public class Chap7_7 {
    public static class MyWorkerThread extends ForkJoinWorkerThread {
        private static ThreadLocal<Integer> taskCounter = new ThreadLocal<>();

        protected MyWorkerThread(ForkJoinPool pool) {
            super(pool);
        }

        @Override
        protected void onStart() {
            super.onStart();
            System.out.printf("MyWorkerThread %d: Initializing task counter.\n", getId());
            taskCounter.set(0);
        }

        @Override
        protected void onTermination(Throwable throwable) {
            System.out.printf("MyWorkerThread %d: %d\n", getId(), taskCounter.get());
            super.onTermination(throwable);
        }

        public void addTask() {
            int counter = taskCounter.get();
            counter++;
            taskCounter.set(counter);
        }
    }

    public static class MyWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new MyWorkerThread(pool);
        }
    }

    public static class MyRecursiveTask extends RecursiveTask<Integer> {
        private int[] array;
        private int start, end;

        public MyRecursiveTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            Integer ret;
            MyWorkerThread thread = (MyWorkerThread) Thread.currentThread();
            thread.addTask();

            if (end - start < 2) {
                ret = array[start];
            } else {
                int mid = (start + end) / 2;
                MyRecursiveTask task1 = new MyRecursiveTask(array, start, mid);
                MyRecursiveTask task2 = new MyRecursiveTask(array, mid, end);
                invokeAll(task1, task2);

                ret = addResults(task1, task2);
            }

            return ret;
        }

        private Integer addResults(MyRecursiveTask task1, MyRecursiveTask task2) {
            int value;
            try {
                value = task1.get() + task2.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                value = 0;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return value;
        }
    }

    public static void main(String[] args) throws Exception {
        MyWorkerThreadFactory factory = new MyWorkerThreadFactory();

        ForkJoinPool pool = new ForkJoinPool(4, factory, null, false);

        int[] array = new int[1000];

        for (int i = 0; i < array.length; i++) {
            array[i] = 1;
        }

        MyRecursiveTask task = new MyRecursiveTask(array, 0, array.length);

        pool.execute(task);

        task.join();

        pool.shutdown();

        pool.awaitTermination(1, TimeUnit.DAYS);

        System.out.printf("Main: Result: %d\n", task.get());
        System.out.printf("Main: End\n");
    }
}
