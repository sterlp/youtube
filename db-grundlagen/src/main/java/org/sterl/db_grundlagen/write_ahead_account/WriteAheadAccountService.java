package org.sterl.db_grundlagen.write_ahead_account;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(timeout = 5)
@RequiredArgsConstructor
public class WriteAheadAccountService {

    private final WriteAheadAccountRepository accountRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    public List<WriteAheadAccountEntity> listAll() {
        return accountRepository.findAll();
    }
    
    public WriteAheadAccountEntity save(WriteAheadAccountEntity account) {
        return accountRepository.save(account);
    }
    
    public int sumAccount(String account) {
        return accountRepository.sumByAccount(account);
    }

    public WriteAheadAccountEntity updateAccount(String account, int ammount) {
        var result = accountRepository.save(new WriteAheadAccountEntity(account, ammount));

        // ensure we hold the lock during the sleep, 
        // only required if the sleep around 250ms
        entityManager.flush(); 
        sleep(500);

        return result;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
