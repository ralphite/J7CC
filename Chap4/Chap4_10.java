package Chap4;

import java.util.concurrent.*;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap4_10 {
    public static class ExecutableTask implements Callable<String> {
        private String name;

        public ExecutableTask(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String call() throws Exception {
            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: Waiting %d seconds for results.\n",
                        this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }

            return "Hello, I'm " + name;
        }
    }

    public static class ResultTask extends FutureTask<String> {
        private String name;

        public ResultTask(Callable<String> callable) {
            super(callable);
            this.name = ((ExecutableTask) callable).getName();
        }

        @Override
        protected void done() {
            if (isCancelled()) {
                System.out.printf("%s: Has been cancelled.\n", name);
            } else {
                System.out.printf("%s: Has finished.\n", name);
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService =
                (ExecutorService) Executors.newCachedThreadPool();

        ResultTask[] resultTasks = new ResultTask[5];

        for (int i = 0; i < 5; i++) {
            ExecutableTask task = new ExecutableTask("Task " + i);
            resultTasks[i] = new ResultTask(task);
            executorService.submit(resultTasks[i]);
        }

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (ResultTask resultTask1 : resultTasks) {
            resultTask1.cancel(true);
        }

        for (ResultTask resultTask : resultTasks) {
            try {
                if (!resultTask.isCancelled()) {
                    System.out.printf("%s\n", resultTask.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }
}
