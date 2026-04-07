package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

class SaleIdTest {
  @Test
  void should_create_sale_id_when_value_is_valid() {
    SaleId saleId = SaleId.of(1L);

    assertEquals(1, saleId.value());
  }

  @Test
  void should_throw_exception_when_sale_id_is_null() {
    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> SaleId.of(null));
    assertEquals("sale id cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_sale_id_is_zero() {
    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> SaleId.of(0L));
    assertEquals("sale id must be greater than zero", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_sale_id_is_negative() {
    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> SaleId.of(-1L));
    assertEquals("sale id must be greater than zero", exception.getMessage());
  }

  @Test
  void should_be_equal_when_sale_id_values_are_the_same() {
    SaleId saleId1 = SaleId.of(1L);
    SaleId saleId2 = SaleId.of(1L);

    assertEquals(saleId1, saleId2);
  }

  @Test
  void should_not_be_equal_when_sale_id_values_are_different() {
    SaleId saleId1 = SaleId.of(1L);
    SaleId saleId2 = SaleId.of(2L);

    assertNotEquals(saleId1, saleId2);
  }
}
