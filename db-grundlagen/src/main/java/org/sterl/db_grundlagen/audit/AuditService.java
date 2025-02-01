package org.sterl.db_grundlagen.audit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.db_grundlagen.audit.model.AuditEntity;
import org.sterl.db_grundlagen.audit.repository.AuditRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    //@Async
    public void newAuditEvent(String name) {
        auditRepository.save(new AuditEntity(name));
    }
}
