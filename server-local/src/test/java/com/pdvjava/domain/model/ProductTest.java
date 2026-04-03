package com.pdvjava.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.Money;
import com.pdvjava.domain.vo.ProductId;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  void should_create_product_when_all_fields_are_valid() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));

    Product product = Product.of(productId, "Coffe", unitPrice);

    assertEquals(productId, product.productId());
    assertEquals("Coffe", product.name());
    assertEquals(0, unitPrice.compareTo(product.unitPrice()));
  }

  @Test
  void should_throw_exception_when_product_id_is_null() {
    Money unitPrice = Money.of(new BigDecimal("10.00"));

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> Product.of(null, "Coffe", unitPrice));
    assertEquals("product id cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_name_is_null() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> Product.of(productId, null, unitPrice));
    assertEquals("product name cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_name_is_blank() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("10.00"));

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> Product.of(productId, "  ", unitPrice));
    assertEquals("product name cannot be blank", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_unit_price_is_null() {
    ProductId productId = ProductId.of(1L);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> Product.of(productId, "Coffe", null));
    assertEquals("product unit price cannot be null", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_unit_price_is_zero() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("0.00"));

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class, () -> Product.of(productId, "Coffe", unitPrice));
    assertEquals("product unit price must be greater than zero", exception.getMessage());
  }

  @Test
  void should_throw_exception_when_unit_price_is_negative() {
    ProductId productId = ProductId.of(1L);
    Money unitPrice = Money.of(new BigDecimal("-1.00"));

    DomainValidationException exception =
        assertThrows(
            DomainValidationException.class, () -> Product.of(productId, "Coffe", unitPrice));
    assertEquals("product unit price must be greater than zero", exception.getMessage());
  }
}
