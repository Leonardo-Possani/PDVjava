package com.pdvjava.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.ProductId;
import com.pdvjava.domain.vo.Quantity;
import com.pdvjava.domain.vo.StockBalance;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StockTest {

  @Test
  void should_create_stock_when_entries_are_valid() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    assertTrue(stock.contains(coffeeId));
    assertTrue(stock.contains(breadId));
    assertEquals(0, coffeeBalance.compareTo(stock.balanceOf(coffeeId)));
    assertEquals(0, breadBalance.compareTo(stock.balanceOf(breadId)));
  }

  @Test
  void should_throw_exception_when_entries_are_null() {

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> Stock.of(null));
    assertEquals("stock entries cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_product_id_is_null_in_entries() {
    Map<ProductId, StockBalance> entries = new HashMap<>();
    entries.put(null, StockBalance.of(10));

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> Stock.of(entries));
    assertEquals("stock product id cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_stock_balance_is_nul_in_entries() {
    Map<ProductId, StockBalance> entries = new HashMap<>();
    entries.put(ProductId.of(1L), null);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> Stock.of(entries));
    assertEquals("stock balance cannot be null", exception.getMessage());
  }

  @Test
  void should_return_boolean_when_product_id_not_found_in_stock() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);
    ProductId otherId = ProductId.of(3L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    assertTrue(stock.contains(coffeeId));
    assertFalse(stock.contains(otherId));
  }

  @Test
  void should_decrease_stock_when_product_exists_and_quantity_is_available() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);
    Quantity quantity = Quantity.of(5);
    Stock newStock = stock.decrease(coffeeId, quantity);

    StockBalance expectedBalance = StockBalance.of(5);

    assertEquals(0, expectedBalance.compareTo(newStock.balanceOf(coffeeId)));
    assertNotEquals(stock.balanceOf(coffeeId).value(), newStock.balanceOf(coffeeId).value());
  }

  @Test
  void should_return_new_instance_when_decreasing_stock() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);
    Quantity quantity = Quantity.of(5);
    Stock newStock = stock.decrease(coffeeId, quantity);

    StockBalance expectedBalance = StockBalance.of(5);

    assertEquals(0, expectedBalance.compareTo(newStock.balanceOf(coffeeId)));
    assertNotSame(stock, newStock);
  }

  @Test
  void should_throw_exception_when_decreasing_stock_with_null_quantity() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stock.decrease(coffeeId, null));
    assertEquals("quantity cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_decreasing_stock_below_zero() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);
    Quantity quantity = Quantity.of(11);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stock.decrease(coffeeId, quantity));
    assertEquals("stock balance cannot be negative after subtraction", exception.getMessage());
  }

  @Test
  void should_return_false_when_stock_does_not_contain_product() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);
    ProductId otherId = ProductId.of(3L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    assertFalse(stock.contains(otherId));
  }

  @Test
  void should_throw_exception_when_balance_of_receives_null_product_id() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stock.balanceOf(null));
    assertEquals("stock product id cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_balance_of_product_id_is_not_found_in_stock() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);
    ProductId otherId = ProductId.of(3L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stock.balanceOf(otherId));
    assertEquals("product not found in stock", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_decreasing_stock_with_null_product_id() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    Quantity quantity = Quantity.of(5);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stock.decrease(null, quantity));
    assertEquals("stock product id cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_decreasing_stock_when_product_is_not_found_in_stock() {
    ProductId coffeeId = ProductId.of(1L);
    ProductId breadId = ProductId.of(2L);
    ProductId otherId = ProductId.of(3L);

    StockBalance coffeeBalance = StockBalance.of(10);
    StockBalance breadBalance = StockBalance.of(5);

    Map<ProductId, StockBalance> entries =
        Map.of(
            coffeeId, coffeeBalance,
            breadId, breadBalance);
    Stock stock = Stock.of(entries);

    Quantity quantity = Quantity.of(5);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> stock.decrease(otherId, quantity));
    assertEquals("product not found in stock", exception.getMessage());
  }
}
