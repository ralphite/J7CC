package Chap6;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap6_4 {
    public static class Event implements Comparable<Event> {
        private int thread;
        private int priority;

        public int getThread() {
            return thread;
        }

        public int getPriority() {
            return priority;
        }

        public Event(int thread, int priority) {

            this.thread = thread;
            this.priority = priority;
        }

        @Override
        public int compareTo(Event e) {
            if (this.priority > e.getPriority()) {
                return -1;
            } else if (this.priority < e.getPriority()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static class Task implements Runnable {
        private int id;
        private PriorityBlockingQueue<Event> queue;

        public Task(int id, PriorityBlockingQueue<Event> queue) {
            this.id = id;
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                Event event = new Event(id, i);
                queue.add(event);
            }
        }
    }

    public static void main(String[] args) {
        PriorityBlockingQueue<Event> queue = new PriorityBlockingQueue<>();
        Thread[] taskThreads = new Thread[5];

        for (int i = 0; i < taskThreads.length; i++) {
            Task task = new Task(i, queue);
            taskThreads[i] = new Thread(task);
        }

        for (Thread taskThread : taskThreads) {
            taskThread.start();
        }

        for (Thread taskThread : taskThreads) {
            try {
                taskThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Queue size: %d\n", queue.size());
        for (int i = 0; i < taskThreads.length * 1000; i++) {
            Event event = queue.poll();
            System.out.printf("Thread %s: Priority %d\n",
                    event.getThread(), event.getPriority());
        }

        System.out.printf("\nMain: Queue size: %d\n", queue.size());
        System.out.println("Main: End of the program.");
    }
}
