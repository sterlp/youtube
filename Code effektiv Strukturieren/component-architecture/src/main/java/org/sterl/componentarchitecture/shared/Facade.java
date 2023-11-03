package org.sterl.componentarchitecture.shared;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(timeout = 10_000)
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Facade {

}
