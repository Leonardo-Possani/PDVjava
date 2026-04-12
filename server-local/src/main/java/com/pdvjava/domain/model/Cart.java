package com.pdvjava.domain.model;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.CartId;
import com.pdvjava.domain.vo.CartStatus;
import com.pdvjava.domain.vo.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class Cart {
    private final CartId cartId;
    private final List<SaleItem> items;
    private final Money totalAmount;
    private final CartStatus status;

    private Cart (CartId cartId, List<SaleItem> items, Money totalAmount, CartStatus status) {
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public static Cart of(CartId cartId) {
        if (cartId == null) {
            throw new DomainValidationException("cart id cannot be null");
        }

        return new Cart(cartId, List.of(), Money.of(new BigDecimal("0.00")), CartStatus.EDITABLE);

    }

    public Cart addItem(SaleItem saleitem) {
        if (saleitem == null) {
            throw new DomainValidationException("sale item cannot be null");
        }
        if (this.status != CartStatus.EDITABLE) {
            throw new DomainValidationException("cart is not editable");
        }

        List<SaleItem> updatedItems = new ArrayList<>(this.items);
        updatedItems.add(saleitem);

        Money updatedTotal = this.totalAmount.plus(saleitem.lineTotal());

        return new Cart(this.cartId, List.copyOf(updatedItems), updatedTotal, this.status);
    }

    public CartId cartId() {
        return this.cartId;
    }

    public List<SaleItem> items() {
        return this.items;
    }

    public Money totalAmount() {
        return this.totalAmount;
    }

    public CartStatus status() {
        return this.status;
    }
}
