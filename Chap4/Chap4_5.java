package J7CC.Chap4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by yawen on 3/31/2015.
 */
public class Chap4_5 {
    public static class UserValidator {
        private String name;

        public UserValidator(String name) {
            this.name = name;
        }

        public boolean validate(String name, String password) {
            Random random = new Random();

            try {
                long duration = (long) (Math.random() * 10);

                System.out.printf("Validator %s: Validating a user during %d seconds\n",
                        this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            return random.nextBoolean();
        }

        public String getName() {
            return name;
        }
    }

    public static class TaskValidator implements Callable<String> {
        private UserValidator validator;

        private String user;
        private String password;

        public TaskValidator(UserValidator validator, String user, String password) {
            this.validator = validator;
            this.user = user;
            this.password = password;
        }

        @Override
        public String call() throws Exception {
            if (!validator.validate(user, password)) {
                System.out.printf("%s: The user has not been found\n",
                        validator.getName());

                throw new Exception("Error validating user.");
            }

            System.out.printf("%s: The user has been found\n",
                    validator.getName());
            return validator.getName();
        }
    }

    public static void main(String[] args) {
        String username = "test";
        String password = "test";

        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DB");

        TaskValidator ldapTask = new TaskValidator(
                ldapValidator, username, password
        );
        TaskValidator dbTask = new TaskValidator(
                dbValidator, username, password
        );

        List<TaskValidator> taskList = new ArrayList<>();
        taskList.add(ldapTask);
        taskList.add(dbTask);

        ExecutorService executorService =
                (ExecutorService) Executors.newCachedThreadPool();

        String result;

        try {
            result = executorService.invokeAny(taskList);
            System.out.printf("Main: Result: %s\n", result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        System.out.printf("Main: End of the Execution.\n");
    }
}
