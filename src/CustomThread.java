import java.util.function.Supplier;

public class CustomThread extends Thread {
    private final Runnable task; // Functional interface to hold the task to execute
    private final long pollingInterval; // Polling interval in milliseconds
    private final Supplier<Boolean> endCondition; // Functional interface to hold the end condition
    private volatile boolean running = true;

    public CustomThread(Runnable task, long pollingInterval, Supplier<Boolean> endCondition) {
        this.task = task;
        this.pollingInterval = pollingInterval;
        this.endCondition = endCondition;
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        while (running && !endCondition.get()) {
            try {
                // Execute the provided task
                task.run();
                // Sleep for the polling interval
                Thread.sleep(pollingInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        stopThread();

    }
}
