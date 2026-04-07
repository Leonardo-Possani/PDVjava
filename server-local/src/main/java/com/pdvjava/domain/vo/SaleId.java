package com.pdvjava.domain.vo;

import com.pdvjava.domain.exception.DomainValidationException;
import java.util.Objects;

public final class SaleId {
  private final Long value;

  private SaleId(Long value) {
    this.value = value;
  }

  public static SaleId of(Long value) {
    if (value == null) {
      throw new DomainValidationException("sale id cannot be null");
    }
    if (value <= 0L) {
      throw new DomainValidationException("sale id must be greater than zero");
    }
    return new SaleId(value);
  }

  public Long value() {
    return this.value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SaleId that)) {
      return false;
    }
    return Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
