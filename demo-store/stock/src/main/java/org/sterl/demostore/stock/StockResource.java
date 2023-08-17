package org.sterl.demostore.stock;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("stock-items")
@RequiredArgsConstructor
public class StockResource {

    private final StockService stockService;

    @PostMapping
    public Collection<Stock> addStock(AddStockRequest request) {
        return stockService.addStock(request.name(), request.count());
    }
    
    @GetMapping
    public Collection<Stock> list() {
        return stockService.list();
    }
}
