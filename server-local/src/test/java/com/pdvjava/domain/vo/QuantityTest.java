package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

class QuantityTest {

  @Test
  void should_create_quantity_when_value_is_greater_than_zero() {
    Quantity quantity = Quantity.of(1);

    assertEquals(1, quantity.value());
  }

  @Test
  void should_throw_domain_validation_exception_when_value_is_zero() {
    assertThrows(DomainValidationException.class, () -> Quantity.of(0));
  }

  @Test
  void should_throw_domain_validation_exception_when_value_is_negative() {
    assertThrows(DomainValidationException.class, () -> Quantity.of(-1));
  }
}
