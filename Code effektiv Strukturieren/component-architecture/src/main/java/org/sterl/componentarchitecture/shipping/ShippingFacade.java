package org.sterl.componentarchitecture.shipping;

import org.springframework.context.event.EventListener;
import org.sterl.componentarchitecture.order.OrderFacade;
import org.sterl.componentarchitecture.order.model.OrderPayedEvent;
import org.sterl.componentarchitecture.shared.Facade;
import org.sterl.componentarchitecture.shipping.model.ShippingEntity;

import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class ShippingFacade {

    private final OrderFacade orderFacade;
    
    @EventListener
    public void shipOrder(OrderPayedEvent orderedEvent) {
        new ShippingEntity(null, "new", orderedEvent.order());
        orderFacade.setOrderTrakingId(orderedEvent.order().getId(), "12345");
    }

    public String listShippings() {
        // TODO Auto-generated method stub
        return null;
    }
}
