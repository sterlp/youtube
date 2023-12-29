package org.sterl.db_grundlagen.write_ahead_account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WriteAheadAccountRepository extends JpaRepository<WriteAheadAccountEntity, Long> {

    @Query("SELECT sum(e.balance) FROM WriteAheadAccountEntity e WHERE e.account = :account")
    int sumByAccount(String account);

}
