package com.pdvjava.domain.vo;

import com.pdvjava.domain.exception.DomainValidationException;
import java.util.Objects;

public final class CartId {
    private final Long value;

    private CartId(Long value) { this.value = value; }

    public static CartId of(Long value) {
        if (value == null) {
            throw new DomainValidationException("cart id cannot be null");
        }
        if (value <= 0L) {
            throw new DomainValidationException("cart id must be greater than zero");
        }
        return new CartId(value);
    }

    public Long value() {return this.value;}

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CartId that)) {
            return false;
        }
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(this.value); }
}
