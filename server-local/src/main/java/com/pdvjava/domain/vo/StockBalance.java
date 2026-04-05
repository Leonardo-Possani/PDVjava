package com.pdvjava.domain.vo;

import com.pdvjava.domain.exception.DomainValidationException;

public final class StockBalance implements Comparable<StockBalance> {
  private final int stockBalance;

  private StockBalance(int stockBalance) {
    this.stockBalance = stockBalance;
  }

  public static StockBalance of(int stockBalance) {
    if (stockBalance < 0) {
      throw new DomainValidationException("stock balance cannot be negative");
    }
    return new StockBalance(stockBalance);
  }

  public StockBalance plus(Quantity other) {
    if (other == null) {
      throw new DomainValidationException("quantity cannot be null");
    }
    return StockBalance.of(this.stockBalance + other.value());
  }

  public StockBalance minus(Quantity other) {
    if (other == null) {
      throw new DomainValidationException("quantity cannot be null");
    }
    if (this.stockBalance - other.value() < 0) {
      throw new DomainValidationException("stock balance cannot be negative after subtraction");
    }
    return StockBalance.of(this.stockBalance - other.value());
  }

  public int value() {
    return this.stockBalance;
  }

  public boolean isZero() {
    return this.stockBalance == 0;
  }

  @Override
  public int compareTo(StockBalance other) {
    if (other == null) {
      throw new DomainValidationException("stock balance cannot be null");
    }
    return Integer.compare(this.stockBalance, other.stockBalance);
  }
}
