package org.sterl.db_grundlagen.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sterl.db_grundlagen.audit.model.AuditEntity;

public interface AuditRepository extends JpaRepository<AuditEntity, Long> {

}
