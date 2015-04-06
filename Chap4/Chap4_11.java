package Chap4;

import java.util.concurrent.*;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap4_11 {
    public static class ReportGenerator implements Callable<String> {
        private String sender;
        private String title;

        public ReportGenerator(String sender, String title) {
            this.sender = sender;
            this.title = title;
        }

        @Override
        public String call() throws Exception {
            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s_%s: ReportGenerator: Generating " +
                                "a report during %d seconds\n",
                        this.sender, this.title, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return sender + ": " + title;
        }
    }

    public static class ReportRequest implements Runnable {
        private String name;
        private CompletionService<String> service;

        public ReportRequest(String name, CompletionService<String> service) {
            this.name = name;
            this.service = service;
        }

        @Override
        public void run() {
            ReportGenerator reportGenerator = new ReportGenerator(name, "Report");
            service.submit(reportGenerator);
        }
    }

    public static class ReportProcessor implements Runnable {
        private CompletionService<String> service;
        private boolean end;

        public ReportProcessor(CompletionService<String> service) {
            this.service = service;
            end = false;
        }

        @Override
        public void run() {
            while (!end) {
                try {
                    Future<String> stringFuture = service.poll(20, TimeUnit.SECONDS);
                    if (stringFuture != null) {
                        String report = stringFuture.get();
                        System.out.printf("ReportReceiver: Report Received: %s\n", report);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("ReportSender: End\n");
        }

        public void setEnd(boolean end) {
            this.end = end;
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService =
                (ExecutorService) Executors.newCachedThreadPool();

        CompletionService<String> service =
                new ExecutorCompletionService<>(executorService);

        ReportRequest faceRequest = new ReportRequest("Face", service);
        ReportRequest onlineRequest = new ReportRequest("Online", service);
        Thread faceThread = new Thread(faceRequest);
        Thread onlineThread = new Thread(onlineRequest);

        ReportProcessor processor = new ReportProcessor(service);
        Thread senderThread = new Thread(processor);

        System.out.printf("Main: Starting the Threads\n");
        faceThread.start();
        onlineThread.start();
        senderThread.start();

        try {
            System.out.printf("Main: Waiting for the report generators.\n");
            faceThread.join();
            onlineThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Shutting down the executor.\n");
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        processor.setEnd(true);
        System.out.println("Main: Ends");
    }
}
