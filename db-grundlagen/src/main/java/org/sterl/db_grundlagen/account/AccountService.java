package org.sterl.db_grundlagen.account;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.account.component.AddMoneyComponent;
import org.sterl.db_grundlagen.account.component.WithdrawMoneyComponent;
import org.sterl.db_grundlagen.account.model.AccountEntity;
import org.sterl.db_grundlagen.account.model.TransferMoneyCommand;
import org.sterl.db_grundlagen.account.repository.AccountRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(timeout = 5)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AddMoneyComponent addMoney;
    private final WithdrawMoneyComponent withdrawMoney;
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
    
    
    public void transferMoney(TransferMoneyCommand command) {
        withdrawMoney.execute(command.from(), command.ammount());
        addMoney.execute(command.to(), command.ammount());
    }

    
    public AccountEntity updateAccount(String id, int ammount) {
        /* same as but not quite due - different exception handling
        var result = accountRepository.findLocked(id).get(); 
         */
        var result = entityManager.find(AccountEntity.class, id, LockModeType.PESSIMISTIC_WRITE, 
                Map.of("jakarta.persistence.lock.timeout", 4500,
                       "jakarta.persistence.query.timeout", 4500));

        result.add(ammount);
        // ensure we hold the lock during the sleep, 
        // only required if the sleep around 250ms
        entityManager.flush(); 
        sleep(250);

        return result;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountEntity create(String account, int balance) {
        if (accountRepository.existsById(account)) throw new IllegalStateException("Account already exists with id=" + account);
        return accountRepository.save(new AccountEntity(account, balance));
    }

    public int getBalance(String account) {
        var r = accountRepository.findById(account);
        if (r.isEmpty()) throw new IllegalArgumentException("Account not found with id=" + account);
        return r.get().getBalance();
    }
}
