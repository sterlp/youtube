package org.sterl.db_grundlagen.account;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(timeout = 5)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

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
        var result = accountRepository.findLocked(id).orElseGet(
                () -> accountRepository.save(new AccountEntity(id, 0)));

        sleep(1_000);

        System.err.println(Instant.now() + ": Updated " + result + " to balance=" + result.add(ammount));

        return result;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {}
    }
}
