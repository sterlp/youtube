package org.sterl.db_grundlagen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
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

    private ExecutorService executor = Executors.newFixedThreadPool(10);
    
    @AfterEach
    void clean() throws InterruptedException {
        synchronized(executor) {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
            executor = Executors.newFixedThreadPool(10);
        }
        accountRepository.deleteAllInBatch();
    }
    
    @Test
    void updateConcurrentUpdate() throws IOException, InterruptedException {
        // GIVEN
        accountService.save(new AccountEntity("a", 0));

        // WHEN
        updateAccountAsync("a", 8);
        updateAccountAsync("a", -10);
        updateAccountAsync("a", 3);
        
        // THEN
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        // AND
        assertThat(accountService.get("a").get().getBalance()).isEqualTo(1);
    }
    
    private void updateAccountAsync(String id, int ammount) {
        executor.submit(() -> {
            try {
                accountService.updateAccount(id, ammount);
            } catch (Exception e) {
                System.err.println("\n----\n"
                                 + "Failed to update id=" + id + " with ammount=" + ammount
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
