package org.sterl.db_grundlagen.account.model;

public record TransferMoneyCommand(String from, String to, int ammount) {

}
