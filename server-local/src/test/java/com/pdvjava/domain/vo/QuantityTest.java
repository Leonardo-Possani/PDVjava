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

  @Test
  void should_compare_quantities_correctly_when_values_are_equal() {
    Quantity quantity1 = Quantity.of(5);
    Quantity quantity2 = Quantity.of(5);

    assertEquals(0, quantity1.compareTo(quantity2));
  }

  @Test
  void should_return_positive_when_current_quantity_is_greater_than_other() {
    Quantity quantity1 = Quantity.of(10);
    Quantity quantity2 = Quantity.of(5);

    assertEquals(1, quantity1.compareTo(quantity2));
  }

  @Test
  void should_throw_domain_validation_exception_when_comparing_with_null() {
    Quantity quantity = Quantity.of(10);

    assertThrows(DomainValidationException.class, () -> quantity.compareTo(null));
  }

  @Test
  void should_return_negative_when_current_quantity_is_less_than_other() {
    Quantity quantity1 = Quantity.of(5);
    Quantity quantity2 = Quantity.of(10);

    assertEquals(-1, quantity1.compareTo(quantity2));
  }
}
