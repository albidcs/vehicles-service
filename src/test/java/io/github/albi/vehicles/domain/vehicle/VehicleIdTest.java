package io.github.albi.vehicles.domain.vehicle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class VehicleIdTest {

    @Test
    void acceptsPositiveId() {
        var id = new VehicleId(1L);
        assertEquals(1L, id.value());
    }

    @Test
    void rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new VehicleId(null));
    }

    @Test
    void rejectsNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> new VehicleId(0L));
        assertThrows(IllegalArgumentException.class, () -> new VehicleId(-1L));
    }
}