package J7CC.Chap4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap4_6 {
    public static class Result {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class Task implements Callable<Result> {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public Result call() throws Exception {
            System.out.printf("%s: Starting\n", this.name);

            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: Waiting %d seconds for the results.\n",
                        this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int value = 0;
            for (int i = 0; i < 5; i++) {
                value += (int) (Math.random() * 100);
            }

            Result result = new Result();
            result.setName(this.name);
            result.setValue(value);

            System.out.println(this.name + ": Ends");

            return result;
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService =
                (ExecutorService) Executors.newCachedThreadPool();

        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Task task = new Task(String.valueOf(i));
            taskList.add(task);
        }

        List<Future<Result>> resultList = null;

        try {
            resultList = executorService.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        System.out.println("Main: Printing the results");
        assert resultList != null;
        for (Future<Result> future : resultList) {
            try {
                Result result = future.get();
                System.out.println(result.getName() + ": " + result.getValue());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
