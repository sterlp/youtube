package org.sterl.db_grundlagen.person.component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.person.model.PersonEntity;
import org.sterl.db_grundlagen.person.repository.PersonRepository;

import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class CreatePersonComponent {
    private final PersonRepository personRepository;

    public PersonEntity execute(PersonEntity result) {
        result = personRepository.save(result);
        personRepository.flush();
        
        if (result.getName() == null || result.getName().isEmpty()) throw new RuntimeException("Name ist leer!");

        return result;
    }
}
