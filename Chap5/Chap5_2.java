package Chap5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap5_2 {
    public static class Product {
        private String name;
        private double price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    public static class ProductListGenerator {
        public List<Product> generate(int size) {
            List<Product> ret = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                Product product = new Product();
                product.setName("Product " + i);
                product.setPrice(10);
                ret.add(product);
            }

            return ret;
        }
    }

    public static class Task extends RecursiveAction {
        private static final long serialVersionUID = 1L;

        private List<Product> products;

        private int first;
        private int last;

        private double increment;

        public Task(List<Product> products, int first, int last, double increment) {
            this.products = products;
            this.first = first;
            this.last = last;
            this.increment = increment;
        }

        @Override
        protected void compute() {
            if (last - first < 10) {
                updatePrices();
            } else {
                int middle = (last + first) / 2;
                System.out.printf("Task: Pending tasks: %s\n", getQueuedTaskCount());
                Task task1 = new Task(products, first, middle + 1, increment);
                Task task2 = new Task(products, middle + 1, last, increment);
                invokeAll(task1, task2);
            }
        }

        private void updatePrices() {
            for (int i = first; i < last; i++) {
                Product product = products.get(i);
                product.setPrice(product.getPrice() * (1 + increment));
            }
        }
    }

    public static void main(String[] args) {
        ProductListGenerator generator = new ProductListGenerator();
        List<Product> products = generator.generate(10000);

        Task task = new Task(products, 0, products.size(), 0.20);

        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);

        do {
            System.out.printf("====Main: Thread count: %d\n", pool.getActiveThreadCount());
            System.out.printf("====Main: Thread steal: %d\n", pool.getStealCount());
            System.out.printf("====Main: Parallelism: %d\n", pool.getParallelism());

            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());

        pool.shutdown();

        if (task.isCompletedNormally()) {
            System.out.printf("Main: The process has completed normally.\n");
        }

        for (Product product : products) {
            if (product.getPrice() != 12) {
                System.out.printf("!!!!!Product %s: %f\n",
                        product.getName(), product.getPrice());
            }
        }

        System.out.printf("Main: End of program.\n");
    }
}
