package org.sterl.db_grundlagen.write_ahead_account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wriate_ahead_accounts")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class WriteAheadAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String account;

    @Column(columnDefinition = "integer default 0", updatable = false)
    private int balance;

    public WriteAheadAccountEntity(String account, int balance) {
        super();
        this.account = account;
        this.balance = balance;
    }
}

