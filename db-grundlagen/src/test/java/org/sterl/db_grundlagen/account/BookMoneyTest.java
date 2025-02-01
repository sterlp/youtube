package org.sterl.db_grundlagen.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.sterl.db_grundlagen.account.model.TransferMoneyCommand;
import org.sterl.test.hibernate_asserts.HibernateAsserts;

import jakarta.persistence.EntityManager;

@SpringBootTest
class BookMoneyTest {

    @Autowired 
    private AccountService subject;
    @Autowired
    private HibernateAsserts hibernateAsserts;
    
    @TestConfiguration
    public static class Config {
        @Bean
        HibernateAsserts hibernateAsserts(EntityManager entityManager) {
            return new HibernateAsserts(entityManager);
        }
    }
    
    @Test
    void testTransferMoney() {
        // GIVEN
        var from = subject.create(UUID.randomUUID().toString(), 50).getId();
        var to = subject.create(UUID.randomUUID().toString(), 0).getId();
        
        // WHEN
        subject.transferMoney(new TransferMoneyCommand(from, to, 30));

        // THEN
        assertThat(subject.getBalance(from)).isEqualTo(20);
        assertThat(subject.getBalance(to)).isEqualTo(30);
    }

}
