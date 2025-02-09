package org.sterl.db_grundlagen.account.component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.account.model.AccountEntity;
import org.sterl.db_grundlagen.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class AddMoneyComponent {

    private final AccountRepository accountRepository;
    
    public AccountEntity execute(String accountId, int ammount) {
        var account = accountRepository.findById(accountId);
        if (account.isEmpty()) throw new IllegalStateException("Account not found " + accountId);
        
        return account.get().add(ammount);
    }

}
