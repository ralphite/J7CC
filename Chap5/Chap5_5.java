package J7CC.Chap5;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap5_5 {
    public static class Task extends RecursiveTask<Integer> {
        private int[] array;
        private int start, end;

        public Task(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            System.out.printf("Task: Start from %d to %d.\n",
                    start, end);

            if (end - start < 10) {
                if (start < 3 && 3 < end) {
                    throw new RuntimeException("This task throws an Runtime Exception:" +
                            " Task from " + start + " to " + end);
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                int mid = (start + end) / 2;
                Task task1 = new Task(array, start, mid);
                Task task2 = new Task(array, mid, end);

                invokeAll(task1, task2);
            }

            System.out.printf("Task: End from %d to %d\n", start, end);

            return 0;
        }
    }

    public static void main(String[] args) {
        int[] array = new int[100];
        Task task = new Task(array, 0, 100);

        ForkJoinPool pool = new ForkJoinPool();

        pool.execute(task);

        pool.shutdown();

        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (task.isCompletedAbnormally()) {
            System.out.printf("Main: An exception has occurred.\n");
            System.out.printf("Main: %s\n", task.getException());
        }

        System.out.printf("Main: Result: %d", task.join());
    }
}
