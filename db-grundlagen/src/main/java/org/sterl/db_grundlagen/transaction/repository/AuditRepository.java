package org.sterl.db_grundlagen.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sterl.db_grundlagen.transaction.model.AuditEntity;

public interface AuditRepository extends JpaRepository<AuditEntity, Long> {

}
