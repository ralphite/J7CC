package J7CC.Chap5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap5_3 {
    public static class DocumentMock {
        private String[] words = {
                "the", "hello", "goodbye", "packt", "java",
                "thread", "pool", "random", "class", "main"
        };

        public String[][] generateDocument(int numLines, int numWords, String word) {
            int counter = 0;
            String[][] document = new String[numLines][numWords];
            Random random = new Random();

            for (int i = 0; i < numLines; i++) {
                for (int j = 0; j < numWords; j++) {
                    int index = random.nextInt(words.length);
                    document[i][j] = words[index];
                    if (document[i][j].equals(word)) {
                        counter++;
                    }
                }
            }

            System.out.println("DocumentMock: The word appears " + counter +
                    " times in the document.");
            return document;
        }
    }

    public static class DocumentTask extends RecursiveTask<Integer> {
        private String[][] document;
        private int start, end;
        private String word;

        public DocumentTask(String[][] document, int start, int end, String word) {
            this.document = document;
            this.start = start;
            this.end = end;
            this.word = word;
        }

        @Override
        protected Integer compute() {
            int result = 0;
            if (end - start < 10) {
                result = processLines(document, start, end, word);
            } else {
                int mid = (start + end) / 2;
                DocumentTask task1 = new DocumentTask(document, start, mid, word);
                DocumentTask task2 = new DocumentTask(document, mid, end, word);
                invokeAll(task1, task2);

                try {
                    result = groupResults(task1.get(), task2.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        private Integer processLines(String[][] document, int start, int end, String word) {
            List<LineTask> tasks = new ArrayList<>();
            for (int i = start; i < end; i++) {
                LineTask task = new LineTask(document[i], 0, document[i].length, word);
                tasks.add(task);
            }

            invokeAll(tasks);

            int result = 0;
            for (LineTask task : tasks) {
                try {
                    result += task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        private Integer groupResults(Integer number1, Integer number2) {
            return number1 + number2;
        }
    }

    public static class LineTask extends RecursiveTask<Integer> {
        private static final long serialVersionUID = 1L;
        private String[] line;
        private int start, end;
        private String word;

        public LineTask(String[] line, int start, int end, String word) {
            this.line = line;
            this.start = start;
            this.end = end;
            this.word = word;
        }

        @Override
        protected Integer compute() {
            Integer result = 0;
            if (end - start < 100) {
                result = count(line, start, end, word);
            } else {
                int mid = (start + end) / 2;
                LineTask task1 = new LineTask(line, start, mid, word);
                LineTask task2 = new LineTask(line, mid, end, word);
                invokeAll(task1, task2);

                try {
                    result = groupResults(task1.get(), task2.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        private Integer count(String[] line, int start, int end, String word) {
            int counter = 0;
            for (int i = start; i < end; i++) {
                if (line[i].equals(word)) {
                    counter++;
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return counter;
        }

        private Integer groupResults(int num1, int num2) {
            return num1 + num2;
        }
    }

    public static void main(String[] args) {
        DocumentMock mock = new DocumentMock();
        String[][] document = mock.generateDocument(100, 1000, "the");
        DocumentTask task = new DocumentTask(document, 0, 100, "the");

        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);

        do {
            System.out.println("*****************************");
            System.out.println("Main: Parallelism: " + pool.getParallelism());
            System.out.println("Main: Active Threads: " + pool.getActiveThreadCount());
            System.out.println("Main: Task Count: " + pool.getQueuedTaskCount());
            System.out.println("Main: Steal Count: " + pool.getStealCount());
            System.out.println("*****************************");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());

        pool.shutdown();

        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.printf("Main: The word appears %d times in the document.\n",
                    task.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
