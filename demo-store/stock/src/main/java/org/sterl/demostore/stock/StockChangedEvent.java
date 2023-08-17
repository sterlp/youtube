package org.sterl.demostore.stock;

public record StockChangedEvent(Stock stock, int count) {

}
