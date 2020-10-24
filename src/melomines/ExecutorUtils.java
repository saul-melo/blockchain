package melomines;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {
    static ExecutorService executorMining;
    static int allotedTime = 60;

    // 1st task submitted is message simulator, rest are the "miners"
    public static void startMining(int minersCount) {
        executorMining = Executors.newFixedThreadPool(minersCount + 1);
        executorMining.submit(MessageFactory::generateConvo);
        for (int i = 0; i < minersCount; i++) {
            executorMining.submit(new CreateBlockTask());
        }
    }

    public static void shutdownAndAwaitTermination(){
        executorMining.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executorMining.awaitTermination(allotedTime, TimeUnit.SECONDS)) {
                System.out.println("\n-----Timeout exceeded - Shutting down mining-----\n");
                executorMining.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executorMining.awaitTermination(allotedTime / 2, TimeUnit.SECONDS))
                    System.err.println("Mining pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorMining.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
