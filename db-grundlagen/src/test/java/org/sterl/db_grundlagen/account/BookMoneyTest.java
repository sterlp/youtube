package org.sterl.db_grundlagen.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sterl.db_grundlagen.HibernateAsserts;
import org.sterl.db_grundlagen.account.model.TransferMoneyCommand;

@SpringBootTest
class BookMoneyTest {

    @Autowired 
    private AccountService subject;
    @Autowired
    private HibernateAsserts hibernateAsserts;
    
    @Test
    void testTransferMoney() {
        // GIVEN
        var from = subject.create(UUID.randomUUID().toString(), 50).getId();
        var to = subject.create(UUID.randomUUID().toString(), 0).getId();
        
        // WHEN
        hibernateAsserts.reset();
        subject.transferMoney(new TransferMoneyCommand(from, to, 30));
        
        // THEN
        hibernateAsserts.assertTrxCount(1);
        assertThat(subject.getBalance(from)).isEqualTo(20);
        assertThat(subject.getBalance(to)).isEqualTo(30);
    }

}
