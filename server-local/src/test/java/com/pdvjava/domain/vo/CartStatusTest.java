package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CartStatusTest {
    @Test
    void should_contain_editable_checkout_started_in_cart_status() {
        CartStatus[] expected = {
                CartStatus.EDITABLE,
                CartStatus.CHECKOUT_STARTED,
        };

        assertArrayEquals(expected, CartStatus.values());
    }

    @Test
    void should_define_editable_status() {
        CartStatus status = CartStatus.valueOf("EDITABLE");

        assertEquals( CartStatus.EDITABLE, status );
    }

    @Test
    void should_define_checkout_started_status() {
        CartStatus status = CartStatus.valueOf("CHECKOUT_STARTED");

        assertEquals( CartStatus.CHECKOUT_STARTED, status );
    }
}
