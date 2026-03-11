package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void should_throw_exception_when_amount_is_null() {
    assertThrows(DomainValidationException.class, () -> Money.of(null));
  }

  @Test
  void should_create_money_with_scale_2_and_half_up_when_amount_has_more_decimals() {
    Money rounded = Money.of(new BigDecimal("10.125"));
    Money expected = Money.of(new BigDecimal("10.13"));

    assertEquals(0, rounded.compareTo(expected));
  }

  @Test
  void should_sum_two_money_values_when_plus_is_called() {
    Money first = Money.of(new BigDecimal("12.40"));
    Money second = Money.of(new BigDecimal("7.60"));

    Money result = first.plus(second);
    Money expected = Money.of(new BigDecimal("20.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_identify_zero_and_negative_values() {
    Money zero = Money.of(new BigDecimal("0.00"));
    Money negative = Money.of(new BigDecimal("-1.00"));
    Money positive = Money.of(new BigDecimal("5.00"));

    assertTrue(zero.isZero());
    assertFalse(zero.isNegative());

    assertTrue(negative.isNegative());
    assertFalse(negative.isZero());

    assertFalse(positive.isZero());
    assertFalse(positive.isNegative());
  }

  @Test
  void should_subtract_two_money_values_when_minus_is_called() {
    Money first = Money.of(new BigDecimal("5.50"));
    Money second = Money.of(new BigDecimal("4.00"));

    Money result = first.minus(second);
    Money expected = Money.of(new BigDecimal("1.50"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_throw_exception_when_minus_argument_is_null() {
    Money value = Money.of(new BigDecimal("5.00"));

    assertThrows(DomainValidationException.class, () -> value.minus(null));
  }

  @Test
  void should_return_negative_value_when_subtraction_result_is_below_zero() {
    Money first = Money.of(new BigDecimal("5.00"));
    Money second = Money.of(new BigDecimal("6.00"));

    Money result = first.minus(second);
    Money expected = Money.of(new BigDecimal("-1.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_zero_when_subtracting_same_values() {
    Money first = Money.of(new BigDecimal("5.00"));
    Money second = Money.of(new BigDecimal("5.00"));

    Money result = first.minus(second);
    Money expected = Money.of(new BigDecimal("0.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_multiply_money_value_when_times_is_called_with_positive_factor() {
    Money first = Money.of(new BigDecimal("5.00"));
    Money second = Money.of(new BigDecimal("5.00"));

    Money result = first.times(second);
    Money expected = Money.of(new BigDecimal("25.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_zero_when_times_is_called_with_zero_factor() {
    Money first = Money.of(new BigDecimal("5.00"));
    Money second = Money.of(new BigDecimal("0.00"));

    Money result = first.times(second);
    Money expected = Money.of(new BigDecimal("0.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_throw_exception_when_times_factor_is_null() {
    Money value = Money.of(new BigDecimal("5.00"));

    assertThrows(DomainValidationException.class, () -> value.times(null));
  }

  @Test
  void should_apply_half_up_rounding_when_times_result_has_more_than_two_decimals() {
    Money first = Money.of(new BigDecimal("0.10"));
    Money second = Money.of(new BigDecimal("0.15"));

    Money result = first.times(second);
    Money expected = Money.of(new BigDecimal("0.02"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_negative_when_money_is_positive_and_factor_is_negative() {
    Money first = Money.of(new BigDecimal("5.00"));
    Money second = Money.of(new BigDecimal("-2.00"));

    Money result = first.times(second);
    Money expected = Money.of(new BigDecimal("-10.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_positive_when_money_is_negative_and_factor_is_negative() {
    Money first = Money.of(new BigDecimal("-5.00"));
    Money second = Money.of(new BigDecimal("-2.00"));

    Money result = first.times(second);
    Money expected = Money.of(new BigDecimal("10.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_current_value_when_current_amount_is_greater_than_other() {
    Money first = Money.of(new BigDecimal("2.00"));
    Money second = Money.of(new BigDecimal("1.00"));

    Money result = first.max(second);
    Money expected = Money.of(new BigDecimal("2.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_other_value_when_other_amount_is_greater_than_current() {
    Money first = Money.of(new BigDecimal("1.00"));
    Money second = Money.of(new BigDecimal("2.00"));

    Money result = first.max(second);
    Money expected = Money.of(new BigDecimal("2.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_current_value_when_both_amounts_are_equal() {
    Money first = Money.of(new BigDecimal("2.00"));
    Money second = Money.of(new BigDecimal("2.00"));

    Money result = first.max(second);
    Money expected = Money.of(new BigDecimal("2.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_throw_exception_when_max_argument_is_null() {
    Money first = Money.of(new BigDecimal("2.00"));

    assertThrows(DomainValidationException.class, () -> first.max(null));
  }

  @Test
  void should_return_zero_when_zero_is_greater_than_negative_value() {
    Money first = Money.of(new BigDecimal("0.00"));
    Money second = Money.of(new BigDecimal("-2.00"));

    Money result = first.max(second);
    Money expected = Money.of(new BigDecimal("0.00"));

    assertEquals(0, result.compareTo(expected));
  }

  @Test
  void should_return_positive_value_when_comparing_positive_and_negative_amounts() {
    Money first = Money.of(new BigDecimal("-2.00"));
    Money second = Money.of(new BigDecimal("2.00"));

    Money result = first.max(second);
    Money expected = Money.of(new BigDecimal("2.00"));

    assertEquals(0, result.compareTo(expected));
  }
}
