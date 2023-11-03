package org.sterl.componentarchitecture.order.command;

import org.springframework.context.ApplicationEventPublisher;
import org.sterl.componentarchitecture.order.model.OrderEntity;
import org.sterl.componentarchitecture.order.model.OrderPayedEvent;
import org.sterl.componentarchitecture.order.repository.OrderRepository;
import org.sterl.componentarchitecture.payment.model.PaymentEntity;
import org.sterl.componentarchitecture.shared.Command;

import lombok.RequiredArgsConstructor;

@Command
@RequiredArgsConstructor
public class OrderPayedCommand {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void call(OrderEntity order, PaymentEntity payment) {
        order.setPayment(payment);
        order = orderRepository.save(order);
        eventPublisher.publishEvent(new OrderPayedEvent(order));
    }

}
