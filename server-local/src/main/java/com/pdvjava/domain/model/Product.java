package com.pdvjava.domain.model;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.Money;
import com.pdvjava.domain.vo.ProductId;

public final class Product {
  private final ProductId productId;
  private final String name;
  private final Money unitPrice;

  private Product(ProductId productId, String name, Money unitPrice) {
    this.productId = productId;
    this.name = name;
    this.unitPrice = unitPrice;
  }

  public static Product of(ProductId productId, String name, Money unitPrice) {
    if (productId == null) {
      throw new DomainValidationException("product id cannot be null");
    } else if (name == null) {
      throw new DomainValidationException("product name cannot be null");
    }
    name = name.trim();
    if (name.isBlank()) {
      throw new DomainValidationException("product name cannot be blank");
    } else if (unitPrice == null) {
      throw new DomainValidationException("product unit price cannot be null");
    } else if (unitPrice.isNegative() || unitPrice.isZero()) {
      throw new DomainValidationException("product unit price must be greater than zero");
    }
    return new Product(productId, name, unitPrice);
  }

  public ProductId productId() {
    return this.productId;
  }

  public String name() {
    return this.name;
  }

  public Money unitPrice() {
    return this.unitPrice;
  }
}
