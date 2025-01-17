package org.sterl.db_grundlagen.account.repository;

import java.util.Optional;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.SpecHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.sterl.db_grundlagen.account.model.AccountEntity;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a2132
    @QueryHints({
        @QueryHint(name = AvailableSettings.JAKARTA_LOCK_TIMEOUT, value = "4500"),
        @QueryHint(name = SpecHints.HINT_SPEC_QUERY_TIMEOUT, value = "4500")
    })
    @Query("SELECT e FROM AccountEntity e WHERE e.id = :id")
    Optional<AccountEntity> findLocked(String id);
}
