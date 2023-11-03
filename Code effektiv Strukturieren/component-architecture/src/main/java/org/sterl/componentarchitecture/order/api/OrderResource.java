package org.sterl.componentarchitecture.order.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sterl.componentarchitecture.order.OrderFacade;
import org.sterl.componentarchitecture.order.api.converter.OrderConverter.ToOrder;
import org.sterl.componentarchitecture.order.api.model.Order;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderFacade orderFacade;

    public List<Order> list() {
        return orderFacade.listOrders().stream().map(s -> ToOrder.INSTANCE.convert(s)).toList();
    }
}
