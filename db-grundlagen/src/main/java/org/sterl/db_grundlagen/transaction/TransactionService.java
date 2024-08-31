package org.sterl.db_grundlagen.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.transaction.component.CreatePersonComponent;
import org.sterl.db_grundlagen.transaction.model.PersonEntity;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final CreatePersonComponent createPerson;
    
    @Transactional(readOnly = true, timeout = 10)
    public PersonEntity create(String name) {
        var result = new PersonEntity(name);
        return createPerson.execute(result);
    }
}
