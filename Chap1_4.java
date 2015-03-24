package J7CC;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_4 {
    public static class PrimeGenerator extends Thread {
        @Override
        public void run() {
            long number = 1L;
            while (true) {
                if (isPrime(number)) {
                    System.out.printf("Number %d is Prime\n", number);

                }
                if (isInterrupted()) {
                    System.out.printf("Interrupted");
                    return;
                }
                number++;
            }
        }

        private boolean isPrime(long number) {
            if (number < 2) return false;
            for (int i = 2; i < number; i++) {
                if (number % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {

        Thread task = new PrimeGenerator();
        task.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        task.interrupt();
    }
}
