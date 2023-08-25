package org.sterl.componentarchitecture.order;

import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.sterl.componentarchitecture.order.command.OrderPayedCommand;
import org.sterl.componentarchitecture.order.command.PayOrderCommand;
import org.sterl.componentarchitecture.order.model.OrderEntity;
import org.sterl.componentarchitecture.order.repository.OrderRepository;
import org.sterl.componentarchitecture.payment.model.MoneyReceivedEvent;
import org.sterl.componentarchitecture.shared.Facade;

import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class OrderFacade {

    private final PayOrderCommand payOrder;
    private final OrderPayedCommand orderPayed;

    private final OrderRepository orderRepository;
    
    public OrderEntity buyOrder(OrderEntity order) {
        return payOrder.call(order);
    }

    public List<OrderEntity> listOrders() {
        return orderRepository.findAll();
    }
    
    @EventListener
    public void orderPayed(MoneyReceivedEvent moneyReceivedEvent) {
        Optional<OrderEntity> order = orderRepository.findByPaymentId(moneyReceivedEvent.payment().getId());

        if (order.isPresent()) {
            orderPayed.call(order.get(), moneyReceivedEvent.payment());
        } // else ist relevant
    }

    public void setOrderTrakingId(Long id, String trakingId) {
        orderRepository.findById(id).ifPresent(e -> {
            e.setTrakingId(trakingId);
        });
    }
}
