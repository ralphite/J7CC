package J7CC.Chap7;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yawen on 4/14/2015.
 */
public class Chap7_11 {
    public static class ParkingCounter extends AtomicInteger {
        private int maxNumber;

        public ParkingCounter(int maxNumber) {
            set(0);
            this.maxNumber = maxNumber;
        }

        public boolean carIn() {
            for (; ; ) {
                int value = get();

                if (value == maxNumber) {
                    System.out.printf("Parking counter: The parking lot is full.\n");
                    return false;
                } else {
                    int newValue = value + 1;
                    boolean changed = compareAndSet(value, newValue);
                    if (changed) {
                        System.out.printf("Parking counter: A car has arrived.\n");
                        return true;
                    }
                }
            }
        }

        public boolean carOut() {
            for (; ; ) {
                int value = get();
                if (value == 0) {
                    System.out.printf("Parking counter: The parking lot is empty.\n");
                    return false;
                } else {
                    int newValue = value - 1;
                    boolean changed = compareAndSet(value, newValue);
                    if (changed) {
                        System.out.printf("Parking counter: A car has left.\n");
                        return true;
                    }
                }
            }
        }
    }

    public static class Sensor1 implements Runnable {
        private ParkingCounter counter;

        public Sensor1(ParkingCounter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            counter.carIn();
            counter.carIn();
            counter.carIn();
            counter.carIn();
            counter.carOut();
            counter.carOut();
            counter.carOut();
            counter.carIn();
            counter.carIn();
            counter.carIn();
        }
    }

    public static class Sensor2 implements Runnable {
        private ParkingCounter counter;

        public Sensor2(ParkingCounter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            counter.carIn();
            counter.carOut();
            counter.carOut();
            counter.carIn();
            counter.carIn();
            counter.carIn();
            counter.carIn();
            counter.carIn();
            counter.carIn();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ParkingCounter counter = new ParkingCounter(5);

        Sensor1 sensor1 = new Sensor1(counter);
        Sensor2 sensor2 = new Sensor2(counter);

        Thread thread1 = new Thread(sensor1);
        Thread thread2 = new Thread(sensor2);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.printf("Main: Number of cars:%d\n",
                counter.get());

    }
}
