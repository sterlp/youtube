package org.sterl.componentarchitecture.order.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.sterl.componentarchitecture.order.api.model.Order;
import org.sterl.componentarchitecture.order.model.OrderEntity;

public class OrderConverter {

    public enum ToOrder implements Converter<OrderEntity, Order> {
        INSTANCE;

        @Override
        public Order convert(OrderEntity source) {
            if (source == null) return null;
            return new Order(source.getId(), source.getName());
        }
    }
}
