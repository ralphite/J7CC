package Chap1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_7 {

    public static class DataSourcesLoader implements Runnable {

        @Override
        public void run() {
            System.out.printf("Beginning data sources loading: %s\n", new Date());
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Data sources loading has finished: %s\n", new Date());
        }
    }

    public static class NetworkConnectionLoader implements Runnable {

        @Override
        public void run() {
            System.out.printf("Beginning network connection loading: %s\n", new Date());
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Network connection loading has finished: %s\n", new Date());
        }
    }

    public static void main(String[] args) {

        DataSourcesLoader dataSourcesLoader = new DataSourcesLoader();
        Thread thread1 = new Thread(dataSourcesLoader, "DataSourceThread");

        NetworkConnectionLoader networkConnectionLoader = new NetworkConnectionLoader();
        Thread thread2 = new Thread(networkConnectionLoader, "NetworkConnectionLoaderThread");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Configuration has been loaded: %s\n", new Date());
    }
}
