package J7CC.Chap3;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/29/2015.
 */
public class Chap3_6 {
    public static class FileSearch implements Runnable {
        private String initPath;
        private String end;
        private List<String> results;
        private Phaser phaser;

        public FileSearch(String initPath, String end, Phaser phaser) {
            this.initPath = initPath;
            this.end = end;
            this.phaser = phaser;
            this.results = new ArrayList<>();
        }

        private void directoryProcess(File file) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        directoryProcess(file1);
                    } else {
                        fileProcess(file1);
                    }
                }
            }
        }

        private void fileProcess(File file) {
            if (file.getName().endsWith(end)) {
                results.add(file.getAbsolutePath());
            }
        }

        private void filterResults() {
            List<String> newResults = new ArrayList<>();
            long actualDate = new Date().getTime();

            for (String result : results) {
                File file = new File(result);
                long fileDate = file.lastModified();

                if (actualDate - fileDate <
                        TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                    newResults.add(result);
                }
            }

            results = newResults;
        }

        private boolean checkResults() {
            if (results.isEmpty()) {
                System.out.printf("%s: Phase %d: 0 results.\n",
                        Thread.currentThread().getName(), phaser.getPhase());
                System.out.printf("%s: Phase %d: End.\n",
                        Thread.currentThread().getName(), phaser.getPhase());
                phaser.arriveAndDeregister();
                return false;
            } else {
                System.out.printf("%s: Phase %d: %d results.\n",
                        Thread.currentThread().getName(), phaser.getPhase(), results.size());
                phaser.arriveAndAwaitAdvance();
                return true;
            }
        }

        private void showInfo() {
            for (String result : results) {
                File file = new File(result);
                System.out.printf("%s: %s\n", Thread.currentThread().getName(),
                        file.getAbsolutePath());
            }
            phaser.arriveAndAwaitAdvance();
        }

        @Override
        public void run() {
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s: Starting.\n", Thread.currentThread().getName());

            File file = new File(initPath);
            if (file.isDirectory()) {
                directoryProcess(file);
            }

            if (!checkResults()) {
                return;
            }

            filterResults();

            if (!checkResults()) {
                return;
            }

            showInfo();
            phaser.arriveAndDeregister();
            System.out.printf("%s: Work completed.\n", Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        Phaser phaser = new Phaser(3);

        FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
        FileSearch apps = new FileSearch("C:\\Program Files", "log", phaser);
        FileSearch docs = new FileSearch("C:\\Documents And Settings", "log", phaser);

        Thread systemThread = new Thread(system, "System");
        systemThread.start();

        Thread appsThread = new Thread(apps, "Apps");
        appsThread.start();

        Thread docsThread = new Thread(docs, "Docs");
        docsThread.start();

        try {
            systemThread.join();
            appsThread.join();
            docsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Terminated: " + phaser.isTerminated());
    }
}
