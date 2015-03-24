package J7CC;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_3 {

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

    public static void main(String[] args) {

        Thread[] threads = new Thread[10];
        Thread.State[] states = new Thread.State[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator(i));
            threads[i].setPriority(i % 2 == 0 ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
            threads[i].setName("My Thread " + i);
        }

        try (FileWriter fileWriter = new FileWriter("c:\\log.txt");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (int i = 0; i < 10; i++) {
                printWriter.println("MAIN: Status of Thread " + i + ":" + threads[i].getState());
            }
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }
            boolean finished = false;
            while (!finished) {
                for (int i = 0; i < 10; i++) {
                    if (threads[i].getState() != states[i]) {
                        writeStateInfo(printWriter, threads[i], states[i]);
                        states[i] = threads[i].getState();
                    }
                }
                finished = true;
                for (int i = 0; i < 10; i++) {
                    finished = finished && (threads[i].getState() == Thread.State.TERMINATED);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeStateInfo(PrintWriter printWriter, Thread thread, Thread.State state) {
        printWriter.printf("Main: Id %d - %s\n", thread.getId(), thread.getName());
        printWriter.printf("Main: Priority %d\n", thread.getPriority());
        printWriter.printf("Main: Old State %s\n", state);
        printWriter.printf("Main: New State %s\n", thread.getState());
        printWriter.printf("Main: **********************************\n");
    }
}
