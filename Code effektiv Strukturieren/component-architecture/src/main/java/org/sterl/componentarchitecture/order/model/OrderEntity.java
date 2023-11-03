package org.sterl.componentarchitecture.order.model;

import org.sterl.componentarchitecture.payment.model.PaymentEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class OrderEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private double price;
    private String trakingId;

    @OneToOne
    private PaymentEntity payment;

}
