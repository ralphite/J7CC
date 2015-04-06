package Chap3;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/29/2015.
 */
public class Chap3_7 {
    public static class MyPhaser extends Phaser {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            switch (phase) {
                case 0:
                    return studentsArrived();
                case 1:
                    return finishFirstExercise();
                case 2:
                    return finishSecondExercise();
                case 3:
                    return finishExam();
                default:
                    return true;
            }
        }

        private boolean studentsArrived() {
            System.out.printf("Phaser: The exam is going to start. The students are ready.\n");
            System.out.printf("Phaser: We have %d students.\n", getRegisteredParties());
            return false;
        }

        private boolean finishFirstExercise() {
            System.out.printf("Phaser: All the students have finished the 1st exercise.\n");
            System.out.printf("Phaser: It's time for the 2nd one.\n");
            return false;
        }

        private boolean finishSecondExercise() {
            System.out.printf("Phaser: All the students have finished the 2nd exercise.\n");
            System.out.printf("Phaser: It's time for the 3rd one.\n");
            return false;
        }

        private boolean finishExam() {
            System.out.printf("Phaser: All the students have finished the 3rd exercise.\n");
            System.out.printf("Phaser: Thank you for your time.\n");
            return true;
        }
    }

    public static class Student implements Runnable {
        private Phaser phaser;

        public Student(Phaser phaser) {
            this.phaser = phaser;
        }

        @Override
        public void run() {
            System.out.printf("%s has arrived to do the exam. %s\n",
                    Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s is going to do the 1st exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            doExercise1();
            System.out.printf("%s has done the 1st exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s is going to do the 2nd exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            doExercise2();
            System.out.printf("%s has done the 2nd exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s is going to do the 3rd exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            doExercise3();
            System.out.printf("%s has done the 3rd exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();
        }

        private void doExercise1() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void doExercise2() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void doExercise3() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MyPhaser myPhaser = new MyPhaser();

        Student[] students = new Student[5];
        for (int i = 0; i < students.length; i++) {
            students[i] = new Student(myPhaser);
            myPhaser.register();
        }

        Thread[] threads = new Thread[students.length];

        for (int i = 0; i < students.length; i++) {
            threads[i] = new Thread(students[i], "Student " + i);
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: The phaser has finished: %s.\n", myPhaser.isTerminated());
    }
}
