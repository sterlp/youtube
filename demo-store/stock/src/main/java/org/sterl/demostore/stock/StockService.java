package org.sterl.demostore.stock;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private final Map<String, Stock> db = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public Collection<Stock> addStock(String name, int count) {
        Stock stock = db.computeIfAbsent(name, (n) -> new Stock(n, 0));
        stock.setCount(stock.getCount() + count);

        eventPublisher.publishEvent(new StockChangedEvent(stock, count));
        return db.values();
    }


    public Collection<Stock> list() {
        return db.values();
    }

    public void shipStock(String name) {
        Stock stock = db.get(name);
        if (stock == null || stock.getCount() <= 0) throw new RuntimeException("We are out of stock of=" + name);

        stock.setCount(stock.getCount() - 1);
        
        eventPublisher.publishEvent(new StockChangedEvent(stock, -1));
    }
}
