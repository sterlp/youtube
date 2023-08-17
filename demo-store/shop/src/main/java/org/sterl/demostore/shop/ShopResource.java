package org.sterl.demostore.shop;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("offers")
@RequiredArgsConstructor
public class ShopResource {

    private final ShopService shopService;

    @GetMapping
    public Collection<Offer> list() {
        return shopService.list();
    }
    
    @PostMapping("{id}")
    public void buy(@PathVariable int id) {
        shopService.buyOffer(id);
    }
}
