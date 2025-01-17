package org.sterl.db_grundlagen.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sterl.db_grundlagen.person.repository.PersonRepository;

@SpringBootTest
class PersonServiceTest {
    @Autowired
    private PersonService subject;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void testCreate() {
        // GIVEN
        personRepository.deleteAllInBatch();
        
        // WHEN
        Long id = subject.create("Paul").getId();
        
        // THEN
        assertThat(personRepository.count()).isEqualTo(1);
        assertThat(personRepository.findById(id).get().getName()).isEqualTo("Paul");
    }

    @Test
    void testCreateError() {
        // GIVEN
        personRepository.deleteAllInBatch();
        
        // WHEN
        assertThrows(RuntimeException.class, () -> subject.create(""));
        
        // THEN
        assertThat(personRepository.count()).isZero();
    }
}
