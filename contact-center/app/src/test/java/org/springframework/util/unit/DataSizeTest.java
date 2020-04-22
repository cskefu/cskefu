package org.springframework.util.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataSizeTest {

    @Test
    void test() {
        DataSize parsed = DataSize.parse("15MB");
        assertEquals(15, parsed.toMegabytes());
    }
}
