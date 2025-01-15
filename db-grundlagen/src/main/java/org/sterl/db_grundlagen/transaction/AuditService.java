package org.sterl.db_grundlagen.transaction;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.transaction.model.AuditEntity;
import org.sterl.db_grundlagen.transaction.repository.AuditRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    @Async
    public void newAuditEvent(String name) {
        auditRepository.save(new AuditEntity(name));
    }
}
