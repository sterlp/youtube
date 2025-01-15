package org.sterl.db_grundlagen.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.transaction.component.CreatePersonComponent;
import org.sterl.db_grundlagen.transaction.model.PersonEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final CreatePersonComponent createPerson;
    private final AuditService auditService;
    
    @Transactional
    public PersonEntity create(String name) {
        var result = new PersonEntity(name);

        auditService.newAuditEvent("Person with name " + name + " created!");
        
        result = createPerson.execute(result);

        return result;
    }
}

