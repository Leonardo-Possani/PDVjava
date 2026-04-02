package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

class ProductIdTest {

  @Test
  void should_create_product_id_when_value_is_greater_than_zero() {
    ProductId productId = ProductId.of(1L);

    assertEquals(1L, productId.value());
  }

  @Test
  void should_throw_domain_validation_exception_when_value_is_zero() {
    assertThrows(DomainValidationException.class, () -> ProductId.of(0L));
  }

  @Test
  void should_throw_domain_validation_exception_when_value_is_negative() {
    assertThrows(DomainValidationException.class, () -> ProductId.of(-1L));
  }

  @Test
  void should_consider_product_ids_equal_when_values_are_the_same() {
    ProductId productId1 = ProductId.of(1L);
    ProductId productId2 = ProductId.of(1L);

    assertEquals(productId1, productId2);
  }

  @Test
  void should_consider_product_ids_different_when_values_are_not_the_same() {
    ProductId productId1 = ProductId.of(1L);
    ProductId productId2 = ProductId.of(2L);

    assertNotEquals(productId1, productId2);
  }

  @Test
  void should_throw_domain_validation_exception_when_value_is_null() {
    assertThrows(DomainValidationException.class, () -> ProductId.of(null));
  }
}
