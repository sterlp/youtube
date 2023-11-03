package org.sterl.componentarchitecture.order.command;

import org.springframework.context.ApplicationEventPublisher;
import org.sterl.componentarchitecture.order.model.OrderEntity;
import org.sterl.componentarchitecture.order.model.OrderedEvent;
import org.sterl.componentarchitecture.order.repository.OrderRepository;
import org.sterl.componentarchitecture.payment.PaymentFacade;
import org.sterl.componentarchitecture.payment.model.PaymentEntity;
import org.sterl.componentarchitecture.shared.Command;

import lombok.RequiredArgsConstructor;

@Command
@RequiredArgsConstructor
public class PayOrderCommand {

    private final ApplicationEventPublisher eventPublisher;
    private final PaymentFacade paymentFacade;
    private final OrderRepository orderRepository;
    
    public OrderEntity call(OrderEntity order) {
        PaymentEntity payment = paymentFacade.createPayment("Payment for order " + order.getName(), order.getId(), order.getPrice());
        OrderEntity result = orderRepository.save(order);
        
        eventPublisher.publishEvent(new OrderedEvent(order));
        
        return result;
    }
}
