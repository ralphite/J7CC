package Chap1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_6 {

    public static class FileClock implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.printf("%s\n", new Date());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.printf("The FileClock has been interrupted.");
                }
            }
        }
    }

    public static void main(String[] args) {

        FileClock fileClock = new FileClock();
        Thread thread = new Thread(fileClock);
        thread.start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread.interrupt();
    }
}
