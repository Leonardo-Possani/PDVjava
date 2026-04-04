package com.pdvjava.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.Money;
import com.pdvjava.domain.vo.ProductId;
import com.pdvjava.domain.vo.Quantity;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SaleItemTest {

  @Test
  void should_create_sale_item_when_all_fields_are_valid() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));
    Quantity quantity = Quantity.of(2);

    SaleItem saleItem = SaleItem.of(productId, "Coffe", unitPrice, quantity);

    assertEquals(productId, saleItem.productId());
    assertEquals("Coffe", saleItem.productName());
    assertEquals(0, unitPrice.compareTo(saleItem.unitPrice()));
    assertEquals(quantity, saleItem.quantity());
  }

  @Test
  void should_trim_product_name_creating_sale_item() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));
    Quantity quantity = Quantity.of(2);

    SaleItem saleItem = SaleItem.of(productId, " Coffe ", unitPrice, quantity);
    String coffe = "Coffe";
    coffe = coffe.trim();
    assertEquals(coffe, saleItem.productName());
  }

  @Test
  void should_throw_exception_when_product_id_is_null() {
    Money unitPrice = Money.of(new BigDecimal("10.00"));
    Quantity quantity = Quantity.of(2);

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class, () -> SaleItem.of(null, "Coffe", unitPrice, quantity));
    assertEquals("sale item product id cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_product_name_is_null() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));
    Quantity quantity = Quantity.of(2);

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class,
            () -> SaleItem.of(productId, null, unitPrice, quantity));
    assertEquals("sale item product name cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_name_is_blank() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));
    Quantity quantity = Quantity.of(2);

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class,
            () -> SaleItem.of(productId, "   ", unitPrice, quantity));
    assertEquals("sale item product name cannot be blank", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_unit_price_is_null() {
    ProductId productId = ProductId.of(1L);
    Quantity quantity = Quantity.of(2);

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class, () -> SaleItem.of(productId, "Coffe", null, quantity));
    assertEquals("sale item unit price cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_unit_price_is_zero() {
    Money unitPrice = Money.of(new BigDecimal("0.00"));
    ProductId productId = ProductId.of(1L);
    Quantity quantity = Quantity.of(2);

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class,
            () -> SaleItem.of(productId, "Coffe", unitPrice, quantity));
    assertEquals("sale item unit price cannot be zero", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_unit_price_is_negative() {
    Money unitPrice = Money.of(new BigDecimal("-1.00"));
    ProductId productId = ProductId.of(1L);
    Quantity quantity = Quantity.of(2);

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class,
            () -> SaleItem.of(productId, "Coffe", unitPrice, quantity));
    assertEquals("sale item unit price cannot be negative", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_quantity_is_null() {
    Money unitPrice = Money.of(new BigDecimal("10.00"));
    ProductId productId = ProductId.of(1L);

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class,
            () -> SaleItem.of(productId, "Coffe", unitPrice, null));
    assertEquals("sale item quantity cannot be null", exception.getMessage());
  }

  @Test
  void should_calculate_line_total_when_sale_item_is_valid() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));
    Quantity quantity = Quantity.of(2);

    SaleItem saleItem = SaleItem.of(productId, "Coffe", unitPrice, quantity);
    Money expected = Money.of(new BigDecimal("20.00"));
    assertEquals(0, expected.compareTo(saleItem.lineTotal()));
  }
}
