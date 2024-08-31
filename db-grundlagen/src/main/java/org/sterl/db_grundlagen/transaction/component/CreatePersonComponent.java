package org.sterl.db_grundlagen.transaction.component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.transaction.model.PersonEntity;
import org.sterl.db_grundlagen.transaction.repository.PersonRepository;

import lombok.RequiredArgsConstructor;

@Component
@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class CreatePersonComponent {
    private final PersonRepository personRepository;

    public PersonEntity execute(PersonEntity result) {
        personRepository.save(result);
        personRepository.flush(); // wird auf die DB gerschrieben
        
        if (result.getName() == null || result.getName().isEmpty()) throw new RuntimeException("Name ist leer!");
        
        return result;
    }
}
