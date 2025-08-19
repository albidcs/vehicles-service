package io.github.albi.vehicles.domain.vehicle;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class VehicleServiceTest {

    // Minimal fake repository to assert contract without Spring/DB.
    private static final class FakeRepo implements VehicleRepository {
        private final Set<Long> deleted = new HashSet<>();

        Vehicle lastCreated;
        Vehicle lastUpdated;
        VehicleId lastDeleted;

        @Override
        public Optional<Vehicle> findById(VehicleId id) {
            if (deleted.contains(id.value())) return Optional.empty();
            if (id.value().equals(1L)) {
                return Optional.of(new Vehicle(
                        new VehicleId(1L),
                        new Vin("11111111111111111"),
                        VehicleType.CAR,
                        "Toyota",
                        "Corolla",
                        2020,
                        FuelType.PETROL,
                        "Blue",
                        "ABC123"
                ));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Vehicle> findByVin(Vin vin) { return Optional.empty(); }

        @Override
        public Optional<Vehicle> findByRegistrationNumber(String registrationNumber) { return Optional.empty(); }

        // ✅ Single source of truth: 7-arg search only
        @Override
        public List<Vehicle> search(String make, String model, Integer year,
                                    VehicleType type, FuelType fuelType,
                                    String vin, String registrationNumber) {

            // If VIN provided, simulate exact match or empty
            if (vin != null && !vin.isBlank()) {
                if ("22222222222222222".equals(vin)) {
                    return List.of(new Vehicle(
                            new VehicleId(2L),
                            new Vin(vin),
                            type != null ? type : VehicleType.CAR,
                            make != null ? make : "Honda",
                            model != null ? model : "Civic",
                            year != null ? year : 2021,
                            fuelType != null ? fuelType : FuelType.DIESEL,
                            "Black",
                            "XYZ987"
                    ));
                }
                return List.of();
            }

            // If registration provided, simulate exact (case-insensitive) or empty
            if (registrationNumber != null && !registrationNumber.isBlank()) {
                if ("xyz987".equalsIgnoreCase(registrationNumber)) {
                    return List.of(new Vehicle(
                            new VehicleId(2L),
                            new Vin("22222222222222222"),
                            type != null ? type : VehicleType.CAR,
                            make != null ? make : "Honda",
                            model != null ? model : "Civic",
                            year != null ? year : 2021,
                            fuelType != null ? fuelType : FuelType.DIESEL,
                            "Black",
                            "XYZ987"
                    ));
                }
                return List.of();
            }

            // Fallback: behave like the old 5-arg path for filter-only searches
            return List.of(new Vehicle(
                    new VehicleId(2L),
                    new Vin("22222222222222222"),
                    type != null ? type : VehicleType.CAR,
                    make != null ? make : "Honda",
                    model != null ? model : "Civic",
                    year != null ? year : 2021,
                    fuelType != null ? fuelType : FuelType.DIESEL,
                    "Black",
                    "XYZ987"
            ));
        }

        @Override
        public Vehicle create(Vin vin, VehicleType type, String make, String model,
                              Integer year, FuelType fuelType,
                              String color, String registrationNumber) {
            lastCreated = new Vehicle(
                    new VehicleId(42L),
                    vin,
                    type,
                    make,
                    model,
                    year,
                    fuelType,
                    color,
                    registrationNumber
            );
            return lastCreated;
        }

        @Override
        public Vehicle update(VehicleId id, Vin vin, VehicleType type, String make, String model,
                              Integer year, FuelType fuelType,
                              String color, String registrationNumber) {
            lastUpdated = new Vehicle(
                    id,
                    vin,
                    type,
                    make,
                    model,
                    year,
                    fuelType,
                    color,
                    registrationNumber
            );
            return lastUpdated;
        }

        @Override
        public void delete(VehicleId id) {
            lastDeleted = id;
            deleted.add(id.value());
        }
    }

    @Test
    void getById_returnsVehicle_whenPresent() {
        var service = new VehicleService(new FakeRepo());
        var v = service.getById(new VehicleId(1L));

        assertEquals("Toyota", v.make());
        assertEquals("Corolla", v.model());
        assertEquals(2020, v.year());
        assertEquals("11111111111111111", v.vin().value());
        assertEquals(VehicleType.CAR, v.type());
        assertEquals(FuelType.PETROL, v.fuelType());
        assertEquals("Blue", v.color());
        assertEquals("ABC123", v.registrationNumber());
    }

    @Test
    void getById_throwsVehicleNotFound_whenAbsent() {
        var service = new VehicleService(new FakeRepo());
        assertThrows(VehicleNotFoundException.class,
                () -> service.getById(new VehicleId(999L)));
    }

    @Test
    void search_returnsFilteredResults() {
        var service = new VehicleService(new FakeRepo());
        var results = service.search("Honda", "Civic", 2021, VehicleType.CAR, FuelType.DIESEL, null, null);

        assertFalse(results.isEmpty());
        var v = results.getFirst();
        assertEquals("Honda", v.make());
        assertEquals("Civic", v.model());
        assertEquals(2021, v.year());
        assertEquals(VehicleType.CAR, v.type());
        assertEquals(FuelType.DIESEL, v.fuelType());
    }

    @Test
    void create_persistsAndReturnsVehicle() {
        var repo = new FakeRepo();
        var service = new VehicleService(repo);

        var created = service.create(
                new Vin("33333333333333333"),
                VehicleType.CAR,
                "BMW",
                "i3",
                2022,
                FuelType.ELECTRIC,
                "White",
                "EV123"
        );

        assertNotNull(created.id());
        assertEquals("33333333333333333", created.vin().value());
        assertEquals("BMW", created.make());
        assertEquals("i3", created.model());
        assertEquals(2022, created.year());
        assertEquals(FuelType.ELECTRIC, created.fuelType());
        assertEquals("White", created.color());
        assertEquals("EV123", created.registrationNumber());
        assertEquals(repo.lastCreated, created);
    }

    @Test
    void update_modifiesAndReturnsVehicle() {
        var repo = new FakeRepo();
        var service = new VehicleService(repo);

        var id = new VehicleId(1L);
        var updated = service.update(
                id,
                new Vin("44444444444444444"),
                VehicleType.CAR,
                "Tesla",
                "Model S",
                2023,
                FuelType.ELECTRIC,
                "Red",
                "TESLA123"
        );

        assertEquals(id, updated.id());
        assertEquals("44444444444444444", updated.vin().value());
        assertEquals("Tesla", updated.make());
        assertEquals("Model S", updated.model());
        assertEquals(2023, updated.year());
        assertEquals(FuelType.ELECTRIC, updated.fuelType());
        assertEquals("Red", updated.color());
        assertEquals("TESLA123", updated.registrationNumber());
        assertEquals(repo.lastUpdated, updated);
    }

    @Test
    void delete_marksEntityDeleted_andSubsequentGetThrows() {
        var repo = new FakeRepo();
        var service = new VehicleService(repo);

        var id = new VehicleId(1L);
        service.delete(id);

        assertEquals(id, repo.lastDeleted);
        assertThrows(VehicleNotFoundException.class, () -> service.getById(id));
    }
}