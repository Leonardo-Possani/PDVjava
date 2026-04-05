package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

class StockBalanceTest {
  @Test
  void should_create_stock_balance_when_value_is_zero() {
    StockBalance stockBalance = StockBalance.of(0);

    assertEquals(0, stockBalance.value());
  }

  @Test
  void should_create_stock_balance_when_value_is_positive() {
    StockBalance stockBalance = StockBalance.of(10);

    assertEquals(10, stockBalance.value());
  }

  @Test
  void should_throw_exception_when_value_is_negative() {
    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> StockBalance.of(-1));
    assertEquals("stock balance cannot be negative", exception.getMessage());
  }

  @Test
  void should_return_true_when_balance_is_zero() {
    StockBalance stockBalance = StockBalance.of(0);

    assertTrue(stockBalance.isZero());
  }

  @Test
  void should_add_quantity_to_stock_balance() {
    StockBalance stockBalance = StockBalance.of(8);
    Quantity quantity = Quantity.of(2);

    StockBalance result = stockBalance.plus(quantity);
    StockBalance expected = StockBalance.of(10);

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_subtract_quantity_from_stock_balance_when_result_is_zero_or_positive() {
    StockBalance stockBalance = StockBalance.of(5);
    Quantity quantity = Quantity.of(5);

    StockBalance result = stockBalance.minus(quantity);
    StockBalance expected = StockBalance.of(0);

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_throw_exception_when_subtraction_is_negative() {
    StockBalance stockBalance = StockBalance.of(5);
    Quantity quantity = Quantity.of(6);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stockBalance.minus(quantity));
    assertEquals("stock balance cannot be negative after subtraction", exception.getMessage());
  }

  @Test
  void should_compare_stock_balance_correctly() {
    StockBalance stockBalance = StockBalance.of(5);
    StockBalance stockBalance2 = StockBalance.of(5);

    assertEquals(0, stockBalance.compareTo(stockBalance2));
  }

  @Test
  void should_throw_exception_when_comparing_stock_balance_with_null() {
    StockBalance stockBalance = StockBalance.of(5);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stockBalance.compareTo(null));
    assertEquals("stock balance cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_adding_null_quantity() {
    StockBalance stockBalance = StockBalance.of(5);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stockBalance.plus(null));
    assertEquals("quantity cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_subtracting_null_quantity() {
    StockBalance stockBalance = StockBalance.of(5);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stockBalance.minus(null));
    assertEquals("quantity cannot be null", exception.getMessage());
  }
}
