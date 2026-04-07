package com.pdvjava.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SaleStatusTest {
  @Test
  void should_contain_open_paid_in_sale_status() {
    SaleStatus[] expected = {
      SaleStatus.OPEN, SaleStatus.PAID,
    };

    assertArrayEquals(expected, SaleStatus.values());
  }

  @Test
  void should_define_open_status() {
    SaleStatus status = SaleStatus.valueOf("OPEN");

    assertEquals(SaleStatus.OPEN, status);
  }

  @Test
  void should_define_paid_status() {
    SaleStatus status = SaleStatus.valueOf("PAID");

    assertEquals(SaleStatus.PAID, status);
  }
}
