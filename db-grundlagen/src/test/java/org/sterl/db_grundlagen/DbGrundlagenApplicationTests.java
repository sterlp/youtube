package org.sterl.db_grundlagen;

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
import org.sterl.db_grundlagen.account.AccountEntity;
import org.sterl.db_grundlagen.account.AccountRepository;
import org.sterl.db_grundlagen.account.AccountService;

@SpringBootTest
class DbGrundlagenApplicationTests {

    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
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
    void updateConcurrentUpdate() throws IOException, InterruptedException {
        // GIVEN
        accountService.save(new AccountEntity("a", 0));

        // WHEN - sleep is 500ms, TRX timeout is 5s / query timeout 4.5s
        // => less than ~10 concurrent threads possible!!!
        final var measure = new TimeMeasure();
        for(int clients = 1; clients <= 10; ++clients) updateAccountAsync("a", 1);
        
        // THEN
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        Duration runtime = measure.stop();
        // AND
        TimeMeasure.print("Update Accounts", runtime);
        assertThat(accountService.get("a").get().getBalance()).isEqualTo(10);
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
