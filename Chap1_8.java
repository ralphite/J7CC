package J7CC;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/24/2015.
 */
public class Chap1_8 {

    private static class Event {
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        private Date date;
        private String event;
    }

    public static class WriterTask implements Runnable {

        private Deque<Event> deque;

        public WriterTask(Deque<Event> deque) {
            this.deque = deque;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                Event event = new Event();
                event.setDate(new Date());
                event.setEvent(String.format("The thread %s has generated an event",
                        Thread.currentThread().getId()));
                deque.addFirst(event);

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class CleanerTask extends Thread {
        private Deque<Event> deque;

        public CleanerTask(Deque<Event> deque) {
            this.deque = deque;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                Date date = new Date();
                clean(date);
            }
        }

        private void clean(Date date) {
            long difference;
            boolean delete;
            if (deque.isEmpty()) {
                return;
            }

            delete = false;
            do {
                Event e = deque.getLast();
                difference = date.getTime() - e.getDate().getTime();
                if (difference > 10000) {
                    System.out.printf("Clearner: %s\n", e.getEvent());
                    deque.removeLast();
                    delete = true;
                }
            } while (difference > 10000);

            if (delete) {
                System.out.printf("Cleaner: Size of the deque: %d\n", deque.size());
            }
        }
    }

    public static void main(String[] args) {
        Deque<Event> deque = new ArrayDeque<>();

        WriterTask writerTask = new WriterTask(deque);
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(writerTask);
            thread.start();
        }

        CleanerTask cleanerTask = new CleanerTask(deque);
        cleanerTask.start();
    }
}
