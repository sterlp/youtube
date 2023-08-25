package org.sterl.componentarchitecture.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sterl.componentarchitecture.payment.model.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

}
