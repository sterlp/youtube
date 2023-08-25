package org.sterl.componentarchitecture.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfiguration {

    @Bean
    String someOrderConfig() {
        return "Foo-Bar";
    }
}
