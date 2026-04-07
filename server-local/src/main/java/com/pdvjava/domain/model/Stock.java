package com.pdvjava.domain.model;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.ProductId;
import com.pdvjava.domain.vo.Quantity;
import com.pdvjava.domain.vo.StockBalance;
import java.util.HashMap;
import java.util.Map;

public final class Stock {
  private final Map<ProductId, StockBalance> entries;

  private Stock(Map<ProductId, StockBalance> entries) {
    this.entries = entries;
  }

  public static Stock of(Map<ProductId, StockBalance> entries) {
    if (entries == null) {
      throw new DomainValidationException("stock entries cannot be null");
    }

    Map<ProductId, StockBalance> normalizedEntries = new HashMap<>();

    for (Map.Entry<ProductId, StockBalance> entry : entries.entrySet()) {
      ProductId productId = entry.getKey();
      StockBalance stockBalance = entry.getValue();

      if (productId == null) {
        throw new DomainValidationException("stock product id cannot be null");
      }
      if (stockBalance == null) {
        throw new DomainValidationException("stock balance cannot be null");
      }
      normalizedEntries.put(productId, stockBalance);
    }
    return new Stock(Map.copyOf(normalizedEntries));
  }

  public boolean contains(ProductId productId) {
    return this.entries.containsKey(productId);
  }

  public StockBalance balanceOf(ProductId productId) {
    if (productId == null) {
      throw new DomainValidationException("stock product id cannot be null");
    }
    if (!(this.entries.containsKey(productId))) {
      throw new DomainValidationException("product not found in stock");
    }
    return this.entries.get(productId);
  }

  public Stock decrease(ProductId productId, Quantity quantity) {
    if (productId == null) {
      throw new DomainValidationException("stock product id cannot be null");
    }
    if (quantity == null) {
      throw new DomainValidationException("quantity cannot be null");
    }

    StockBalance currentBalance = entries.get(productId);
    if (currentBalance == null) {
      throw new DomainValidationException("product not found in stock");
    }

    StockBalance updatedBalance = currentBalance.minus(quantity);

    Map<ProductId, StockBalance> updatedEntries = new HashMap<>(entries);
    updatedEntries.put(productId, updatedBalance);

    return new Stock(Map.copyOf(updatedEntries));
  }
}
