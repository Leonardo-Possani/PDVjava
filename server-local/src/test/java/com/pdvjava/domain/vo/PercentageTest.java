package com.pdvjava.domain.vo;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;


class PercentageTest {

    @Test
    void should_create_percentage_when_value_is_zero () {
        Percentage percentage = Percentage.of(new BigDecimal("0"));

        assertEquals(0, percentage.value().compareTo(new BigDecimal("0.00")));
    }

}
