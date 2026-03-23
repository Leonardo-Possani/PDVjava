package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PaymentMethodTest {

  @Test
  void should_contain_cash_debit_and_credit_and_pix_as_supported_payment_methods() {
    PaymentMethod[] expected = {
      PaymentMethod.CASH, PaymentMethod.DEBIT, PaymentMethod.CREDIT, PaymentMethod.PIX,
    };

    assertArrayEquals(expected, PaymentMethod.values());
  }

  @Test
  void should_resolve_cash_when_value_of_cash_is_called() {
    PaymentMethod method = PaymentMethod.valueOf("CASH");

    assertEquals(PaymentMethod.CASH, method);
  }

  @Test
  void should_resolve_debit_when_value_of_debit_is_called() {
    PaymentMethod method = PaymentMethod.valueOf("DEBIT");

    assertEquals(PaymentMethod.DEBIT, method);
  }

  @Test
  void should_resolve_credit_when_value_of_credit_is_called() {
    PaymentMethod method = PaymentMethod.valueOf("CREDIT");

    assertEquals(PaymentMethod.CREDIT, method);
  }

  @Test
  void should_resolve_pix_when_value_of_pix_is_called() {
    PaymentMethod method = PaymentMethod.valueOf("PIX");

    assertEquals(PaymentMethod.PIX, method);
  }

  @Test
  void should_throw_exception_when_value_of_unknown_payment_method_is_called() {
    assertThrows(IllegalArgumentException.class, () -> PaymentMethod.valueOf("DINHEIRO"));
  }

  @Test
  void should_throw_exception_when_value_of_null_payment_method_is_called() {
    assertThrows(NullPointerException.class, () -> PaymentMethod.valueOf(null));
  }
}
