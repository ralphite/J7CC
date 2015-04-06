package Chap3;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by yawen on 3/29/2015.
 */
public class Chap3_5 {
    public static class MatrixMock {
        private int[][] data;

        public MatrixMock(int size, int length, int number) {
            int counter = 0;
            data = new int[size][length];
            Random random = new Random();

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < length; j++) {
                    data[i][j] = random.nextInt(10);
                    if (data[i][j] == number) {
                        counter++;
                    }
                }
            }

            System.out.printf("Mock: There are %d occurrences of %d in generated data.\n",
                    counter, number);
        }

        public int[] getRow(int row) {
            if ((row >= 0) && (row < data.length)) {
                return data[row];
            }
            return null;
        }
    }

    public static class Results {
        private int[] data;

        public Results(int size) {
            this.data = new int[size];
        }

        public void setData(int position, int value) {
            data[position] = value;
        }

        public int[] getData() {
            return data;
        }
    }

    public static class Searcher implements Runnable {
        private int firstRow;
        private int lastRow;
        private MatrixMock mock;
        private Results results;
        private int number;
        private final CyclicBarrier barrier;

        public Searcher(int firstRow, int lastRow, MatrixMock mock,
                        Results results, int number, CyclicBarrier barrier) {
            this.barrier = barrier;
            this.number = number;
            this.results = results;
            this.mock = mock;
            this.lastRow = lastRow;
            this.firstRow = firstRow;
        }

        @Override
        public void run() {
            int counter;

            System.out.printf("%s: Processing lines from %d to %d.\n",
                    Thread.currentThread().getName(), firstRow, lastRow);

            for (int i = firstRow; i < lastRow; i++) {
                int[] row = mock.getRow(i);
                counter = 0;
                for (int aRow : row) {
                    if (aRow == number) {
                        counter++;
                    }
                }
                results.setData(i, counter);
            }

            System.out.printf("%s: Lines processed.\n", Thread.currentThread().getName());

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Grouper implements Runnable {
        private Results results;

        public Grouper(Results results) {
            this.results = results;
        }

        @Override
        public void run() {
            int finalResult = 0;
            System.out.printf("Grouper: Processing results...\n");

            int[] data = results.getData();
            for (int number : data) {
                finalResult += number;
            }

            System.out.printf("Grouper: Total Result: %d.\n", finalResult);
        }
    }

    public static void main(String[] args) {
        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5;
        final int LINES_PARTICIPANT = 2000;

        MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);
        Results results = new Results(ROWS);
        Grouper grouper = new Grouper(results);

        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);

        Searcher[] searchers = new Searcher[PARTICIPANTS];
        for (int i = 0; i < PARTICIPANTS; i++) {
            searchers[i] = new Searcher(i * LINES_PARTICIPANT, (i * LINES_PARTICIPANT) +
                    LINES_PARTICIPANT, mock, results, 5, barrier);

            Thread thread = new Thread(searchers[i]);
            thread.start();
        }

        System.out.printf("Main: The main thread has finished.\n");
    }
}
