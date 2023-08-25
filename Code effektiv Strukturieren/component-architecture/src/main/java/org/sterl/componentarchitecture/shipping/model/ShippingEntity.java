package org.sterl.componentarchitecture.shipping.model;

import org.sterl.componentarchitecture.order.model.OrderEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class ShippingEntity {
    @GeneratedValue
    @Id
    private Long id;

    private String status;

    @OneToOne(cascade = {}, fetch = FetchType.LAZY)
    private OrderEntity order;
}
