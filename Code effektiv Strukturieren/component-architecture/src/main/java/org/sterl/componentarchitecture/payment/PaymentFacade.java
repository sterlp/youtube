package org.sterl.componentarchitecture.payment;

import org.sterl.componentarchitecture.payment.model.PaymentEntity;
import org.sterl.componentarchitecture.payment.repository.PaymentRepository;
import org.sterl.componentarchitecture.shared.Facade;

import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentRepository paymentRepository;
    
    public PaymentEntity createPayment(String name, Long orderId, double price) {
        return paymentRepository.save(new PaymentEntity(null, name, orderId, price));
    }

}
