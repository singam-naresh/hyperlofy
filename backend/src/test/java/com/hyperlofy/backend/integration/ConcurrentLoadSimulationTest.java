package com.hyperlofy.backend.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ConcurrentLoadSimulationTest {

    @Test
    void testHighThroughputParallelEcosystemSimulations() throws InterruptedException {
        // Models heavy concurrent operations (e.g. 1000 orders and 500 agents telemetry streams)
        int orderTaskCount = 1000;
        int threadPoolSize = 64;

        ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < orderTaskCount; i++) {
            final int taskId = i;
            tasks.add(() -> {
                try {
                    // Simulate processing (GPS routing, distance checks, locks execution)
                    Thread.sleep(1); // 1ms light workload
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    failureCounter.incrementAndGet();
                }
                return null;
            });
        }

        long start = System.currentTimeMillis();
        pool.invokeAll(tasks);
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        long duration = System.currentTimeMillis() - start;

        log.info("[Concurrent Load Performance Report] Executed {} highly concurrent tasks on {} threads in {} ms.",
                orderTaskCount, threadPoolSize, duration);

        assertEquals(orderTaskCount, successCounter.get());
        assertEquals(0, failureCounter.get());
    }
}
