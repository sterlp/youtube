package org.sterl.demostore.shop;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.sterl.demostore.stock.Stock;
import org.sterl.demostore.stock.StockChangedEvent;

@Component
public class ShopOfferComponent {

    private final Map<Integer, Offer> db = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    
    public void addOffer(Stock stock) {
        if (findByStock(stock).findAny().isEmpty()) {
            int id = idGenerator.incrementAndGet();
            db.put(id, new Offer(id, stock.getName(), 5.99, stock));
        }
    }

    private Stream<Offer> findByStock(Stock stock) {
        return db.values().stream().filter(o -> o.getName().equals(stock.getName()));
    }
    
    @EventListener
    public void onStockChanged(StockChangedEvent e) {
        if (e.count() > 0) {
            addOffer(e.stock());
        } else {
            Optional<Offer> offer = findByStock(e.stock()).findAny();
            if (offer.isPresent()) db.remove(offer.get().getId());
        }
    }
    
    public Collection<Offer> list() {
        return db.values();
    }

    public Offer get(int id) {
        return db.get(id);
    }
}

