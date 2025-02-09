package org.sterl.db_grundlagen.account.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class AccountEntity {
    @Id
    private String id;
    @Column(columnDefinition = "integer default 0")
    private int balance;
    
    public AccountEntity add(int ammount) {
        balance += ammount;
        return this;
    }

    public AccountEntity subtract(int ammount) {
        balance -= ammount;
        return this;
    }
}
