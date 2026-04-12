package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

class CartIdTest {
    @Test
    void should_create_cart_id_when_value_is_valid() {
        CartId cartId = CartId.of(1L);

        assertEquals( 1, cartId.value());
    }

    @Test
    void should_throw_exception_when_cart_id_is_null() {
        DomainValidationException exception =
                assertThrows(DomainValidationException.class, () -> CartId.of(null));
        assertEquals("cart id cannot be null", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_cart_id_is_zero() {
        DomainValidationException exception =
                assertThrows(DomainValidationException.class, () -> CartId.of(0L));
        assertEquals("cart id must be greater than zero", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_cart_id_is_negative() {
        DomainValidationException exception =
                assertThrows(DomainValidationException.class, () -> CartId.of(-1L));
        assertEquals("cart id must be greater than zero", exception.getMessage());
    }

    @Test
    void should_be_equal_when_cart_id_values_are_the_same() {
        CartId cartId1 = CartId.of(1L);
        CartId cartId2 = CartId.of(1L);

        assertEquals(cartId1, cartId2);
    }

    @Test
    void should_not_be_equal_when_cart_id_values_are_different() {
        CartId cartId1 = CartId.of(1L);
        CartId cartId2 = CartId.of(2L);

        assertNotEquals(cartId1, cartId2);
    }
}
