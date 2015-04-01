package J7CC.Chap5;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap5_4 {
    public static class FolderProcessor extends RecursiveTask<List<String>> {
        private static final long serialVersionUID = 1L;

        private String path;
        private String extension;

        public FolderProcessor(String path, String extension) {
            this.path = path;
            this.extension = extension;
        }

        @Override
        protected List<String> compute() {
            List<String> list = new ArrayList<>();
            List<FolderProcessor> tasks = new ArrayList<>();

            File file = new File(path);
            File[] files = file.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        FolderProcessor task =
                                new FolderProcessor(f.getAbsolutePath(), extension);
                        task.fork();
                        tasks.add(task);
                    } else {
                        if (checkFile(f.getName())) {
                            list.add(f.getAbsolutePath());
                        }
                    }
                }

            }

            if (tasks.size() > 50) {
                System.out.printf("%s: %s tasks ran.\n", file.getAbsolutePath(),
                        tasks.size());
            }

            addResultsFromTasks(list, tasks);

            return list;
        }

        private void addResultsFromTasks(List<String> list,
                                         List<FolderProcessor> tasks) {
            for (FolderProcessor folderProcessor : tasks) {
                list.addAll(folderProcessor.join());
            }
        }

        private boolean checkFile(String name) {
            return name.endsWith(extension);
        }
    }

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();

        FolderProcessor system = new FolderProcessor("C:\\Windows", "log");
        FolderProcessor apps = new FolderProcessor("C:\\Program Files", "log");
        FolderProcessor docs = new FolderProcessor("C:\\Documents and Settings", "log");

        pool.execute(system);
        pool.execute(apps);
        pool.execute(docs);

        do {
            System.out.println("*****************************");
            System.out.println("Main: Parallelism: " + pool.getParallelism());
            System.out.println("Main: Active Threads: " + pool.getActiveThreadCount());
            System.out.println("Main: Queued Tasks Count: " + pool.getQueuedTaskCount());
            System.out.println("Main: Steal Count: " + pool.getStealCount());
            System.out.println("*****************************");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ((!system.isDone()) || (!apps.isDone()) || (!docs.isDone()));

        pool.shutdown();

        List<String> results;
        results = system.join();
        System.out.printf("System: %d files found.\n", results.size());
        results = apps.join();
        System.out.printf("Apps: %d files found.\n", results.size());
        results = docs.join();
        System.out.printf("Docs: %d files found.\n", results.size());
    }
}
