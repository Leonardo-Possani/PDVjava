package com.pdvjava.domain.vo;

import com.pdvjava.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Percentage implements Comparable<Percentage> {
    private final BigDecimal percentage;

    private Percentage(BigDecimal percentage) { this.percentage = percentage; }

    public static Percentage of(BigDecimal percentage) {
        if (percentage == null) {throw new DomainValidationException("percentage cannot be null");}

        BigDecimal normalized = percentage.setScale(2, RoundingMode.HALF_UP);

        if (normalized.compareTo(BigDecimal.ZERO) < 0 || normalized.compareTo(new  BigDecimal("100.00")) > 0) {
            throw new DomainValidationException("discount percentage out of range");
        }
        return new Percentage(normalized);
    }

    public BigDecimal value() {
        return percentage;
    }

    @Override
    public int compareTo(Percentage other) {
        if (other == null) {throw new DomainValidationException("other cannot be null");}
        return percentage.compareTo(other.percentage);
    }
}
