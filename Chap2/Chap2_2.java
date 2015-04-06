package Chap2;

/**
 * Created by yawen on 3/27/2015.
 */
public class Chap2_2 {
    public static class Account {
        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        private double balance;

        public synchronized void addAmount(double amount) {
            double tmp = balance;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tmp += amount;
            balance = tmp;
        }

        public synchronized void subtractAmount(double amount) {
            double tmp = balance;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tmp -= amount;
            balance = tmp;
        }
    }

    public static class Bank implements Runnable {

        private Account account;

        public Bank(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                account.subtractAmount(1000);
            }
        }
    }

    public static class Company implements Runnable {

        private Account account;

        public Company(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                account.addAmount(1000);
            }
        }
    }

    public static void main(String[] args) {
        Account account = new Account();
        account.setBalance(1000);

        Company company = new Company(account);
        Thread companyThread = new Thread(company);

        Bank bank = new Bank(account);
        Thread bankThread = new Thread(bank);

        System.out.printf("Account: Initial balance: %f\n", account.getBalance());

        companyThread.start();
        bankThread.start();

        try {
            companyThread.join();
            bankThread.join();
            System.out.printf("Account: Final balance: %f\n", account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
