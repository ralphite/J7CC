package Chap7;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by ralph on 4/6/15 5:57 PM.
 */
public class Chap7_6 {
    public static class MyScheduledTask<V> extends FutureTask<V>
            implements RunnableScheduledFuture<V> {
        private RunnableScheduledFuture<V> task;
        private ScheduledThreadPoolExecutor executor;

        private long period;
        private long startDate;

        public MyScheduledTask(Runnable runnable, V result,
                               RunnableScheduledFuture<V> task,
                               ScheduledThreadPoolExecutor executor) {
            super(runnable, result);
            this.task = task;
            this.executor = executor;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            if (!isPeriodic()) {
                return task.getDelay(unit);
            } else {
                if (startDate == 0) {
                    return task.getDelay(unit);
                } else {
                    Date now = new Date();
                    long delay = startDate - now.getTime();

                    return unit.convert(delay, TimeUnit.MILLISECONDS);
                }
            }
        }

        @Override
        public int compareTo(Delayed o) {
            return task.compareTo(o);
        }

        @Override
        public boolean isPeriodic() {
            return task.isPeriodic();
        }

        @Override
        public void run() {
            if (isPeriodic() && (!executor.isShutdown())) {
                Date now = new Date();
                startDate = now.getTime() + period;
                executor.getQueue().add(this);
            }

            System.out.printf("Pre-MyScheduledTask: %s\n", new Date());
            System.out.printf("MyScheduledTask: Is Periodic: %s\n", isPeriodic());
            super.runAndReset();
            System.out.printf("Post-MyScheduledTask: %s\n", new Date());
        }

        public void setPeriod(long period) {
            this.period = period;
        }
    }

    public static class MyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
        public MyScheduledThreadPoolExecutor(int corePoolSize) {
            super(corePoolSize);
        }

        @Override
        protected <V> RunnableScheduledFuture<V> decorateTask(
                Runnable runnable,
                RunnableScheduledFuture<V> task
        ) {
            return new MyScheduledTask<>(
                    runnable, null, task, this
            );
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,
                                                      long period, TimeUnit unit) {
            ScheduledFuture<?> task = super.scheduleAtFixedRate(
                    command, initialDelay, period, unit
            );
            MyScheduledTask<?> myScheduledTask = (MyScheduledTask<?>) task;
            myScheduledTask.setPeriod(TimeUnit.MILLISECONDS.convert(period, unit));
            return task;
        }
    }

    public static class Task implements Runnable {
        @Override
        public void run() {
            System.out.printf("Task: Begin.\n");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Task: End.\n");
        }
    }

    public static void main(String[] args) {
        MyScheduledThreadPoolExecutor executor = new MyScheduledThreadPoolExecutor(2);
        Task task = new Task();
        System.out.printf("Main: %s\n", new Date());
        executor.schedule(task, 1, TimeUnit.SECONDS);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task = new Task();
        System.out.printf("Main: %s\n", new Date());
        executor.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main: End of program.");
    }
}
