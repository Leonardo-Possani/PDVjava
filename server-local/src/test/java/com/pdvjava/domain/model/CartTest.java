package com.pdvjava.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import com.pdvjava.domain.vo.CartId;

import com.pdvjava.domain.vo.CartStatus;
import com.pdvjava.domain.vo.Money;
import com.pdvjava.domain.vo.ProductId;
import com.pdvjava.domain.vo.Quantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class CartTest {
    @Test
    void should_create_cart_when_cart_id_is_valid() {
        CartId cartId = CartId.of(1L);

        Cart cart = Cart.of(cartId);

        assertEquals(cartId, cart.cartId());
    }

    @Test
    void should_start_with_editable_status_when_cart_is_created() {
        CartId cartId = CartId.of(1L);

        Cart cart = Cart.of(cartId);

        assertEquals(CartStatus.EDITABLE, cart.status());
    }

    @Test
    void should_start_with_empty_items_when_cart_is_created() {
        CartId cartId = CartId.of(1L);

        Cart cart = Cart.of(cartId);
        assertTrue(cart.items().isEmpty());
    }

    @Test
    void should_start_with_zero_total_amount_when_cart_is_created() {
        CartId cartId = CartId.of(1L);

        Cart cart = Cart.of(cartId);

        assertEquals(0, Money.of(new BigDecimal("0.00")).compareTo(cart.totalAmount()));
    }

    @Test
    void should_throw_exception_when_cart_id_is_null() {
        DomainValidationException exception =
                assertThrows(DomainValidationException.class, () -> Cart.of(null));
        assertEquals("cart id cannot be null", exception.getMessage());
    }

    @Test
    void should_add_item_when_product_is_not_in_cart() {
        CartId cartId = CartId.of(1L);

        ProductId productId = ProductId.of(1L);
        Money unitPrice = Money.of(new BigDecimal("10.00"));
        Quantity quantity = Quantity.of(2);

        SaleItem saleItem = SaleItem.of(productId, "Coffe", unitPrice, quantity);

        Cart cart = Cart.of(cartId);

        Cart updatedCart = cart.addItem(saleItem);

        assertNotSame(cart, updatedCart);
        assertEquals(1, updatedCart.items().size());
        assertEquals(ProductId.of(1L), updatedCart.items().get(0).productId());
        assertEquals("Coffe", updatedCart.items().get(0).productName());
        assertEquals(0, Money.of(new BigDecimal("20.00")).compareTo(updatedCart.totalAmount()));
    }

    @Test
    void should_recalculate_total_amount_when_adding_first_item() {
        Cart cart = Cart.of(CartId.of(1L));

        SaleItem Coffee =
                SaleItem.of(
                        ProductId.of(1L),
                        "Coffee",
                        Money.of(new BigDecimal("10.00")),
                        Quantity.of(2)
                        );
        Cart updatedCart = cart.addItem(Coffee);

        assertEquals(0, Money.of(new BigDecimal("0.00")).compareTo(cart.totalAmount()));
        assertEquals(0, Money.of(new BigDecimal("20.00")).compareTo(updatedCart.totalAmount()));

    }

    @Test
    void should_throw_exception_when_adding_null_item() {
        Cart cart = Cart.of(CartId.of(1L));

        DomainValidationException exception =
                assertThrows(DomainValidationException.class, () -> cart.addItem(null));
        assertEquals("sale item cannot be null", exception.getMessage());
    }

}
