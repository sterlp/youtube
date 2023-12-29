package org.sterl.db_grundlagen.write_ahead_account;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sterl.db_grundlagen.TimeMeasure;

@SpringBootTest
class WriteAheadAccountTests {

    @Autowired WriteAheadAccountService accountService;
    @Autowired WriteAheadAccountRepository accountRepository;

    private final static int THREAD_COUNT = 100;
    private ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    @AfterEach
    void clean() throws InterruptedException {
        synchronized(executor) {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
            executor = Executors.newFixedThreadPool(THREAD_COUNT);
        }
        accountRepository.deleteAllInBatch();
    }
    
    @Test
    void concurrentAccountUpdateTest() throws IOException, InterruptedException {
        // GIVEN
        accountService.save(new WriteAheadAccountEntity("a", 0));
        // AND sleep is 500ms, TRX timeout is 5s / query timeout 4.5s
        final var clientCount = 20;
        
        // WHEN we run concurrent updates
        final var measure = new TimeMeasure();
        for(int clients = 1; clients <= clientCount; ++clients) updateAccountAsync("a", 1);
        
        // THEN
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        Duration runtime = measure.stop();
        // AND
        TimeMeasure.print("Update Accounts", runtime);
        assertThat(accountService.sumAccount("a")).isEqualTo(clientCount);
    }

    private void updateAccountAsync(String id, int ammount) {
        executor.submit(() -> {
            final var measure = new TimeMeasure();
            try {
                accountService.updateAccount(id, ammount);
                Duration duration = measure.stop();
                System.err.println(Instant.now() + ": Updated id=" + id 
                        + " by="+ ammount
                        + " in=" + duration.toMillis() + "ms");
            } catch (Exception e) {
                Duration duration = measure.stop();
                System.err.println("\n----\n"
                                 + Instant.now() + ": Failed to update id=" + id 
                                 + " by=" + ammount
                                 + " in=" + duration.toMillis() + "ms"
                                 + "\n" + e.getMessage()
                                 + "\n----");
            }
        });
    }

    @Test
    void read() {
        accountService.listAll().forEach(System.out::println);
    }
}
