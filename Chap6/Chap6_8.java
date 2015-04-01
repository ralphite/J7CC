package J7CC.Chap6;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yawen on 4/1/2015.
 */
public class Chap6_8 {
    public static class Account {
        private AtomicLong balance;

        public long getBalance() {
            return balance.get();
        }

        public void setBalance(long balance) {
            this.balance.set(balance);
        }

        public Account() {

            this.balance = new AtomicLong();
        }

        public void addAmount(long amount) {
            this.balance.getAndAdd(amount);
        }

        public void subtractAmount(long amount) {
            this.balance.getAndAdd(-amount);
        }
    }

    public static class Company implements Runnable {
        private Account account;

        public Company(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                account.addAmount(1000);
            }
        }
    }

    public static class Bank implements Runnable {
        private Account account;

        public Bank(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                account.subtractAmount(1000);
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

        System.out.printf("Account: Initial Balance: %d\n",
                account.getBalance());

        companyThread.start();
        bankThread.start();

        try {
            companyThread.join();
            bankThread.join();

            System.out.printf("Account: Final Balance: %d\n",
                    account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
