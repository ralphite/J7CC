package Chap6;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap6_3 {
    public static class Client implements Runnable {
        private LinkedBlockingQueue<String> requestList;

        public Client(LinkedBlockingQueue<String> requestList) {
            this.requestList = requestList;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    try {
                        requestList.put(String.valueOf(i) + ":" + String.valueOf(j));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.printf("Client: %s at %s.\n",
                            String.valueOf(i) + ":" + String.valueOf(j), new Date());
                }
            }

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Client: End.\n");
        }
    }

    public static void main(String[] args) throws Exception {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(3);

        Client client = new Client(queue);
        Thread thread = new Thread(client);
        thread.start();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                String request = queue.take();
                System.out.printf("Main: Request: %s at %s. Size: %d\n",
                        request, new Date(), queue.size());
            }

            TimeUnit.MILLISECONDS.sleep(300);
        }

        System.out.printf("Main: End of the program.\n");
    }
}
