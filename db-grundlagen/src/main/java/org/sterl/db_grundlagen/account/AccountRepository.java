package org.sterl.db_grundlagen.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "4500"),
        @QueryHint(name = "jakarta.persistence.query.timeout", value = "4500")
    })
    @Query("SELECT e FROM AccountEntity e WHERE e.id = :id")
    Optional<AccountEntity> findLocked(String id);
}
