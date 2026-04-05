package com.pdvjava.domain.model;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.Money;
import com.pdvjava.domain.vo.ProductId;
import com.pdvjava.domain.vo.Quantity;

public final class SaleItem {
  private final ProductId productId;
  private final Money unitPrice;
  private final Quantity quantity;
  private final String productName;
  private final Money lineTotal;

  private SaleItem(
      ProductId productId,
      String productName,
      Money unitPrice,
      Quantity quantity,
      Money lineTotal) {
    this.productId = productId;
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
    this.lineTotal = lineTotal;
  }

  public static SaleItem of(
      ProductId productId, String productName, Money unitPrice, Quantity quantity) {
    if (productId == null) {
      throw new DomainValidationException("sale item product id cannot be null");
    }
    if (productName == null) {
      throw new DomainValidationException("sale item product name cannot be null");
    }
    productName = productName.trim();
    if (productName.isBlank()) {
      throw new DomainValidationException("sale item product name cannot be blank");
    }
    if (unitPrice == null) {
      throw new DomainValidationException("sale item unit price cannot be null");
    }
    if (unitPrice.isZero()) {
      throw new DomainValidationException("sale item unit price cannot be zero");
    }
    if (unitPrice.isNegative()) {
      throw new DomainValidationException("sale item unit price cannot be negative");
    }
    if (quantity == null) {
      throw new DomainValidationException("sale item quantity cannot be null");
    }

    Money lineTotal = unitPrice.times(quantity);
    return new SaleItem(productId, productName, unitPrice, quantity, lineTotal);
  }

  public ProductId productId() {
    return productId;
  }

  public Money unitPrice() {
    return unitPrice;
  }

  public Quantity quantity() {
    return quantity;
  }

  public String productName() {
    return productName;
  }

  public Money lineTotal() {
    return lineTotal;
  }
}
