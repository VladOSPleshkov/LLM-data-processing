package test;

import analysis.controller.TasksController;
import auth.controller.UserController;
import com.shared.dto.analysis.CreateTaskRequest;
import com.shared.dto.auth.CreateUserRequest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyTestRunner {
    public static void runTasksConcurrencyTest(TasksController tasksController, int threadCount) throws InterruptedException {
        System.out.println("\n========== CONCURRENCY TEST: analysis_service (" + threadCount + " потоков) ==========");
        runConcurrent(threadCount, i ->
                tasksController.processStartAnalysis(new CreateTaskRequest(
                        UUID.randomUUID(), "Brand-" + i, "Параллельный запрос №" + i, 5))
        );
        System.out.println("Итоговое количество задач в хранилище:");
        System.out.println(tasksController.processGetAll());
    }

    public static void runUsersConcurrencyTest(UserController userController, int threadCount) throws InterruptedException {
        System.out.println("\n========== CONCURRENCY TEST: auth_service (" + threadCount + " потоков) ==========");
        runConcurrent(threadCount, i ->
                userController.processCreate(new CreateUserRequest(
                        "concurrent_user_" + i, "kc-sub-" + i, "user" + i + "@example.com", List.of()))
        );
        System.out.println("Итоговое количество пользователей в хранилище:");
        System.out.println(userController.processGetAll());
    }

    private static void runConcurrent(int threadCount, IntFunctionThrowing task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch finishGate = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        List<String> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startGate.await(); // все потоки стартуют одновременно
                    String result = task.apply(idx);
                    results.add(result);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("Поток " + idx + " завершился с ошибкой: " + e.getMessage());
                } finally {
                    finishGate.countDown();
                }
            });
        }

        startGate.countDown(); // залп — все потоки одновременно обращаются к ConnectionHolder
        boolean completed = finishGate.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("Завершено вовремя: " + completed);
        System.out.println("Успешных вызовов: " + successCount.get() + " / " + threadCount);
        System.out.println("Ошибок: " + failureCount.get());
    }

    @FunctionalInterface
    private interface IntFunctionThrowing {
        String apply(int i) throws Exception;
    }
}
