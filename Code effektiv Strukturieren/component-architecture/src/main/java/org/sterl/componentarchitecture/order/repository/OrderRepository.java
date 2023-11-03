package org.sterl.componentarchitecture.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sterl.componentarchitecture.order.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByPaymentId(Long id);

}
