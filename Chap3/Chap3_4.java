package J7CC.Chap3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/29/2015.
 */
public class Chap3_4 {
    public static class VideoConference implements Runnable {
        private final CountDownLatch controller;

        public VideoConference(int number) {
            this.controller = new CountDownLatch(number);
        }

        public void arrive(String name) {
            System.out.printf("%s has arrived.\n", name);
            controller.countDown();
            System.out.printf("VideoConference: Waiting for %d participants.\n",
                    controller.getCount());
        }

        @Override
        public void run() {
            System.out.printf("VideoConference: Initialization: %d participants.\n",
                    controller.getCount());

            try {
                controller.await();
                System.out.printf("VideoConference: All the participants have arrived.\n");
                System.out.printf("VideoConference: Let's start...\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Participant implements Runnable {
        private VideoConference conference;
        private String name;

        public Participant(VideoConference conference, String name) {
            this.conference = conference;
            this.name = name;
        }

        @Override
        public void run() {
            long duration = (long) (Math.random() * 10);
            try {
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            conference.arrive(name);
        }
    }

    public static void main(String[] args) {
        VideoConference conference = new VideoConference(10);
        Thread conferenceThread = new Thread(conference);
        conferenceThread.start();

        for (int i = 0; i < 10; i++) {
            Participant p = new Participant(conference, "Participant " + i);
            Thread t = new Thread(p);
            t.start();
        }
    }
}
