package org.sterl.db_grundlagen.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sterl.db_grundlagen.audit.repository.AuditRepository;
import org.sterl.db_grundlagen.person.PersonService;
import org.sterl.db_grundlagen.person.repository.PersonRepository;

@SpringBootTest
class AuditServiceTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private AuditRepository auditRepository;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void testAuditOnError() throws InterruptedException {
        // GIVEN
        auditRepository.deleteAllInBatch();
        personRepository.deleteAllInBatch();
        
        // WHEN
        assertThrows(RuntimeException.class, () -> personService.create(""));
        
        // THEN
        assertThat(auditRepository.count()).isOne();
        assertThat(personRepository.count()).isZero();
    }

    @Test
    void testAuditOn() {
        // GIVEN
        auditRepository.deleteAllInBatch();
        personRepository.deleteAllInBatch();
        
        // WHEN
        personService.create("Paul");
        
        // THEN
        assertThat(auditRepository.count()).isOne();
        assertThat(personRepository.count()).isOne();
    }
}
