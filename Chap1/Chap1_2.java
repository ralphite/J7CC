package Chap1;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_2 {

    public static class Calculator implements Runnable {

        private int number;

        public Calculator(int number) {
            this.number = number;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.printf("%s : %d * %d = %d\n",
                        Thread.currentThread().getName(), number, i, number * i);
            }
        }

    }

    public static class Calculator2 extends Thread {
        private int number;

        public Calculator2(int number) {
            this.number = number;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                Thread thread = Thread.currentThread();
                System.out.printf("%s : %d * %d = %d\n",
                        String.valueOf(thread.getId()) + ":" +
                                thread.getName() + ":" +
                                String.valueOf(thread.getPriority()) + ":" +
                                thread.getState()
                        , number, i, number * i);
            }
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Calculator calculator = new Calculator(i);
            Thread thread = new Thread(calculator);
            thread.start();
        }

        for (int i = 0; i < 10; i++) {
            Calculator2 calculator2 = new Calculator2(i);
            calculator2.start();
        }
    }
}

