package org.sterl.db_grundlagen.person;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.audit.AuditService;
import org.sterl.db_grundlagen.person.component.CreatePersonComponent;
import org.sterl.db_grundlagen.person.model.PersonEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final CreatePersonComponent createPerson;
    private final AuditService auditService;
    
    @Transactional(propagation = Propagation.NEVER)
    public PersonEntity create(String name) {
        var result = new PersonEntity(name);
        // TRX 1
        auditService.newAuditEvent("Person with name " + name + " created!");
        // TRX 2
        result = createPerson.execute(result);
        
        // HTTP connector do stuff

        return result;
    }
}

