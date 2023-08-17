package org.sterl.demostore.shop;

import org.sterl.demostore.stock.Stock;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Offer {

    private int id;
    private String name;
    private double price;
    private Stock stock;
}
