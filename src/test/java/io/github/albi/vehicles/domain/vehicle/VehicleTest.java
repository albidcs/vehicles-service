package io.github.albi.vehicles.domain.vehicle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class VehicleTest {

    private static Vehicle newValidVehicle() {
        return new Vehicle(
                new VehicleId(1L),
                new Vin("1HGCM82633A123456"),
                VehicleType.CAR,
                "Toyota",
                "Corolla",
                2020,
                FuelType.PETROL,
                "Blue",
                "ABC-123"
        );
    }

    @Test
    void constructsWithValidData() {
        var v = newValidVehicle();
        assertEquals("Toyota", v.make());
        assertEquals("Corolla", v.model());
        assertEquals(2020, v.year());
        assertEquals(VehicleType.CAR, v.type());
        assertEquals(FuelType.PETROL, v.fuelType());
        assertEquals("Blue", v.color());
        assertEquals("ABC-123", v.registrationNumber());
    }

    @Test
    void rejectsNullId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(
                        null,
                        new Vin("1HGCM82633A123456"),
                        VehicleType.CAR,
                        "Toyota",
                        "Corolla",
                        2020,
                        FuelType.PETROL,
                        "Blue",
                        "ABC-123"
                ));
    }

    @Test
    void rejectsBlankMake() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(
                        new VehicleId(1L),
                        new Vin("1HGCM82633A123456"),
                        VehicleType.CAR,
                        " ",
                        "Corolla",
                        2020,
                        FuelType.PETROL,
                        "Blue",
                        "ABC-123"
                ));
    }

    @Test
    void rejectsBlankModel() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(
                        new VehicleId(1L),
                        new Vin("1HGCM82633A123456"),
                        VehicleType.CAR,
                        "Toyota",
                        " ",
                        2020,
                        FuelType.PETROL,
                        "Blue",
                        "ABC-123"
                ));
    }

    @Test
    void rejectsPreAutomobileYear() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(
                        new VehicleId(1L),
                        new Vin("1HGCM82633A123456"),
                        VehicleType.CAR,
                        "Toyota",
                        "Corolla",
                        1800,
                        FuelType.PETROL,
                        "Blue",
                        "ABC-123"
                ));
    }
}