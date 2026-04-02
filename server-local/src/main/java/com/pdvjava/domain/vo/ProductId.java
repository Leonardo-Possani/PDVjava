package com.pdvjava.domain.vo;

import com.pdvjava.domain.exception.DomainValidationException;
import java.util.Objects;

public final class ProductId {
  private final Long value;

  private ProductId(Long value) {
    this.value = value;
  }

  public static ProductId of(Long value) {
    if (value == null) {
      throw new DomainValidationException("product id cannot be null");
    }
    if (value <= 0) {
      throw new DomainValidationException("product id must be greater than zero");
    }
    return new ProductId(value);
  }

  public Long value() {
    return this.value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof ProductId that)) {
      return false;
    }
    return Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
