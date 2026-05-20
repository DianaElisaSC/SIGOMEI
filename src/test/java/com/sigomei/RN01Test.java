package com.sigomei;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import com.sigomei.service.impl.OrdenServiceImpl;

public class RN01Test {

    @Test
    void debeFallarRN01() {

        OrdenServiceImpl service =
                new OrdenServiceImpl();

        assertDoesNotThrow(() -> {

            service.registrarOrden(null);

        });
    }
}