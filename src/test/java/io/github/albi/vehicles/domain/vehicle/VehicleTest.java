package io.github.albi.vehicles.domain.vehicle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class VehicleTest {

    @Test
    void constructsWithValidData() {
        var v = new Vehicle(new VehicleId(1L), "Toyota", "Corolla", 2020);
        assertEquals("Toyota", v.make());
        assertEquals("Corolla", v.model());
        assertEquals(2020, v.year());
    }

    @Test
    void rejectsNullId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(null, "Toyota", "Corolla", 2020));
    }

    @Test
    void rejectsBlankMake() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(new VehicleId(1L), " ", "Corolla", 2020));
    }

    @Test
    void rejectsBlankModel() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(new VehicleId(1L), "Toyota", " ", 2020));
    }

    @Test
    void rejectsPreAutomobileYear() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(new VehicleId(1L), "Toyota", "Corolla", 1800));
    }
}