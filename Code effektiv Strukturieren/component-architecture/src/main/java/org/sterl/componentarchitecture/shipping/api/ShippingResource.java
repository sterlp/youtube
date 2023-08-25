package org.sterl.componentarchitecture.shipping.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sterl.componentarchitecture.shipping.ShippingFacade;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("shippings")
@RequiredArgsConstructor
public class ShippingResource {

    private final ShippingFacade shippingFacade;
    
    public String list() {
        return shippingFacade.listShippings();
    }
}
