package org.sterl.db_grundlagen.account;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(timeout = 5)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    public List<AccountEntity> listAll() {
        return accountRepository.findAll();
    }
    
    public AccountEntity save(AccountEntity account) {
        return accountRepository.save(account);
    }
    
    public Optional<AccountEntity> get(String id) {
        return accountRepository.findById(id);
    }

    public AccountEntity updateAccount(String id, int ammount) {
        var result = accountRepository.findLocked(id).get(); 
        /* same as
        var result = entityManager.find(AccountEntity.class, id, LockModeType.PESSIMISTIC_WRITE, 
                Map.of("jakarta.persistence.lock.timeout", 4500,
                       "jakarta.persistence.query.timeout", 4500));
                       */
                       

        result.add(ammount);
        sleep(500);

        return result;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {}
    }
}
