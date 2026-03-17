package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.pdvjava.domain.exception.DomainValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PercentageTest {

  @Test
  void should_create_percentage_when_value_is_zero() {
    Percentage percentage = Percentage.of(new BigDecimal("0"));

    assertEquals(0, percentage.value().compareTo(new BigDecimal("0.00")));
  }

  @Test
  void should_throw_domain_validation_exception_when_percentage_is_null() {
    assertThrows(DomainValidationException.class, () -> Percentage.of(null));
  }

  @Test
  void should_throw_domain_validation_exception_when_percentage_is_negative() {
    assertThrows(DomainValidationException.class, () -> Percentage.of(new BigDecimal("-1.00")));
  }

  @Test
  void should_throw_domain_validation_exception_when_percentage_is_greater_than_hundred() {
    assertThrows(DomainValidationException.class, () -> Percentage.of(new BigDecimal("110.00")));
  }

  @Test
  void should_create_percentage_when_value_is_hundred() {
    Percentage percentage = Percentage.of(new BigDecimal("100.00"));

    assertEquals(0, percentage.value().compareTo(new BigDecimal("100.00")));
  }

  @Test
  void should_apply_half_up_when_percentage_has_more_than_two_decimal_places() {
    Percentage percentage = Percentage.of(new BigDecimal("4.997"));

    assertEquals(0, percentage.value().compareTo(new BigDecimal("5.00")));
  }

  @Test
  void should_compare_percentages_correctly_when_values_are_equal() {
    Percentage percentage1 = Percentage.of(new BigDecimal("10.00"));
    Percentage percentage2 = Percentage.of(new BigDecimal("10.00"));

    assertEquals(0, percentage1.compareTo(percentage2));
  }

  @Test
  void should_return_positive_when_current_percentage_is_greater_than_other() {
    Percentage percentage1 = Percentage.of(new BigDecimal("15.00"));
    Percentage percentage2 = Percentage.of(new BigDecimal("10.00"));

    assertEquals(1, percentage1.compareTo(percentage2));
  }

  @Test
  void should_throw_domain_validation_exception_when_comparing_with_null() {
    Percentage percentage = Percentage.of(new BigDecimal("10.00"));
    assertThrows(DomainValidationException.class, () -> percentage.compareTo(null));
  }
}
