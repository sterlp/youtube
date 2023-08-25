package org.sterl.componentarchitecture.payment.command;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.sterl.componentarchitecture.payment.model.MoneyReceivedEvent;
import org.sterl.componentarchitecture.payment.model.PaymentEntity;
import org.sterl.componentarchitecture.shared.Command;

import lombok.RequiredArgsConstructor;

@Command
@RequiredArgsConstructor
public class GetTheMoneyCommand {

    private final ApplicationEventPublisher eventPublisher;
    
    @Async
    // because the payment itself is async, it should run in its own transaction
    @Transactional(propagation = Propagation.REQUIRED)
    public void getMoneyFor(PaymentEntity payment) {
        // hole das gelt
        // warte bis es da ist
        // und dann gibt es dieses event
        eventPublisher.publishEvent(new MoneyReceivedEvent(payment));
    }
}
