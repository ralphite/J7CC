package J7CC.Chap1;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_9 {
    public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.printf("An exception  has been captured.\n");
            System.out.printf("Thread: %s\n", t.getId());
            System.out.printf("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
            System.out.printf("Stack Trace: \n");
            e.printStackTrace(System.out);
            System.out.printf("Thread status: %s\n", t.getState());
        }
    }

    public static class Task implements Runnable {

        @Override
        public void run() {
            int n = Integer.parseInt("TT");
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new Task());
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();
    }
}
