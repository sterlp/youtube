package org.sterl.demostore.shop;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.sterl.demostore.stock.StockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final StockService stockService;
    private final ShopOfferComponent offerComponent;
    
    public Collection<Offer> list() {
        return offerComponent.list();
    }

    public void buyOffer(int id) {
        Offer offer = offerComponent.get(id);
        if (offer == null) throw new RuntimeException("Offer with id=" + id + " not found.");
        stockService.shipStock(offer.getStock().getName());
    }

}
