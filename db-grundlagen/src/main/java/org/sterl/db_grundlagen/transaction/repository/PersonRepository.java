package org.sterl.db_grundlagen.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.transaction.model.PersonEntity;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    
    @Transactional
    void deleteByName(String name);

}
