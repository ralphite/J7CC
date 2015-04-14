package J7CC.Chap7;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * Created by yawen on 4/13/2015.
 */
public class Chap7_8 {
    public static abstract class MyWorkerTask extends ForkJoinTask<Void> {
        private String name;

        protected MyWorkerTask(String name) {
            this.name = name;
        }

        @Override
        public Void getRawResult() {
            return null;
        }

        @Override
        protected void setRawResult(Void value) {

        }

        @Override
        protected boolean exec() {
            Date startDate = new Date();
            compute();
            Date finishDate = new Date();
            long diff = finishDate.getTime() - startDate.getTime();
            System.out.printf("MyWorkerTask: %s : %d Milliseconds to complete.\n",
                    name, diff);
            return true;
        }

        public String getName() {
            return name;
        }

        protected abstract void compute();
    }

    public static class Task extends MyWorkerTask {
        private int[] array;
        private int start, end;

        public Task(String name, int[] array, int start, int end) {
            super(name);
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start > 100) {
                int mid = (start + end) / 2;
                Task task1 = new Task(this.getName() + "1", array, start, mid);
                Task task2 = new Task(this.getName() + "2", array, mid, end);
                invokeAll(task1, task2);
            } else {
                for (int i = start; i < end; i++) {
                    array[i]++;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = new int[10000];

        ForkJoinPool pool = new ForkJoinPool();
        Task task = new Task("Task", arr, 0, arr.length);

        pool.invoke(task);

        pool.shutdown();

        System.out.printf("Main: End of program.\n");
    }
}
