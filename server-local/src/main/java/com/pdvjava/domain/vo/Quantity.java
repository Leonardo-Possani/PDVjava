package com.pdvjava.domain.vo;

import com.pdvjava.domain.exception.DomainValidationException;

public final class Quantity implements Comparable<Quantity> {
  private final int value;

  private Quantity(int value) {
    this.value = value;
  }

  public static Quantity of(int value) {
    if (value <= 0) {
      throw new DomainValidationException("quantity must be greater than zero");
    }
    return new Quantity(value);
  }

  public int value() {
    return value;
  }

  @Override
  public int compareTo(Quantity other) {
    if (other == null) {
      throw new DomainValidationException("quantity to compare cannot be null");
    }
    return Integer.compare(this.value, other.value);
  }
}
