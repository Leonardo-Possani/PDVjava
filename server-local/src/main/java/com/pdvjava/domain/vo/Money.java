package com.pdvjava.domain.vo;

import com.pdvjava.domain.exception.DomainValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money implements Comparable<Money> {
  private final BigDecimal amount;

  private Money(BigDecimal amount) {
    this.amount = amount;
  }

  public static Money of(BigDecimal amount) {
    if (amount == null) {
      throw new DomainValidationException("money amount cannot be null");
    }

    BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
    return new Money(normalized);
  }

  public Money plus(Money other) {
    if (other == null) {
      throw new DomainValidationException("money amount cannot be null");
    }
    return Money.of(this.amount.add(other.amount));
  }

  public boolean isZero() {
    return this.amount.compareTo(BigDecimal.ZERO) == 0;
  }

  public boolean isNegative() {
    return this.amount.compareTo(BigDecimal.ZERO) < 0;
  }

  public Money minus(Money other) {
    if (other == null) {
      throw new DomainValidationException("money amount cannot be null");
    }
    return Money.of(this.amount.subtract(other.amount));
  }

  public Money times(Money other) {
    if (other == null) {
      throw new DomainValidationException("money amount cannot be null");
    }
    return Money.of(this.amount.multiply(other.amount));
  }

  public Money max(Money other) {
    if (other == null) {
      throw new DomainValidationException("money amount cannot be null");
    }
    return Money.of(this.amount.max(other.amount));
  }

  @Override
  public int compareTo(Money other) {
    if (other == null) {
      throw new DomainValidationException("money amount cannot be null");
    }
    return this.amount.compareTo(other.amount);
  }
}
