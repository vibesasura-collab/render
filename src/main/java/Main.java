import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("=== SYNC BOT READY (2 CHROME MODE) ===");

        // split accounts
        Queue<Integer> q1 = new LinkedList<>();
        Queue<Integer> q2 = new LinkedList<>();

        for (int i = 21; i <= 30; i++) {
            if (i % 2 == 0) q2.add(i);
            else q1.add(i);
        }

        // start workers
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(new AccountWorker("W1", q1));
        executor.submit(new AccountWorker("W2", q2));

        // WAIT UNTIL JOIN WINDOW OPENS ⏳
        while (true) {

            Thread.sleep(3000);

            System.out.println("Checking arena...");

            // SIMPLE TIME TRIGGER PLACEHOLDER
            // replace with your real check logic if needed

            boolean arenaOpen = true; // set this when you detect join link

            if (arenaOpen) {
                System.out.println("🔥 START SIGNAL SENT");
                SharedSignal.START = true;
                break;
            }
        }

        executor.shutdown();
    }
}
