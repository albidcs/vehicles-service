package io.github.albi.vehicles.domain.vehicle;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

final class VehicleServiceTest {

    // Minimal fake repository to assert contract without Spring or a DB.
    private static final class FakeRepo implements VehicleRepository {
        final AtomicReference<VehicleId> lastFindById = new AtomicReference<>();
        final AtomicReference<String> lastMake = new AtomicReference<>();
        final AtomicReference<String> lastModel = new AtomicReference<>();
        final AtomicReference<Integer> lastYear = new AtomicReference<>();

        @Override
        public Optional<Vehicle> findById(VehicleId id) {
            lastFindById.set(id);
            if (id.value().equals(1L)) {
                return Optional.of(new Vehicle(new VehicleId(1L), "Toyota", "Corolla", 2020));
            }
            return Optional.empty();
        }

        @Override
        public List<Vehicle> search(String make, String model, Integer year) {
            lastMake.set(make);
            lastModel.set(model);
            lastYear.set(year);
            return List.of(new Vehicle(new VehicleId(1L), "Toyota", "Corolla", 2020));
        }

        @Override
        public Vehicle create(String make, String model, Integer year) {
            lastMake.set(make);
            lastModel.set(model);
            lastYear.set(year);
            return new Vehicle(new VehicleId(42L), make, model, year);
        }
    }

    @Test
    void getById_returnsVehicle_whenPresent() {
        var repo = new FakeRepo();
        var service = new VehicleService(repo);

        var v = service.getById(new VehicleId(1L));

        assertEquals("Toyota", v.make());
        assertEquals(1L, repo.lastFindById.get().value());
    }

    @Test
    void getById_throwsVehicleNotFound_whenAbsent() {
        var service = new VehicleService(new FakeRepo());
        assertThrows(VehicleNotFoundException.class, () -> service.getById(new VehicleId(999L)));
    }

    @Test
    void search_forwardsFilters_andReturnsResults() {
        var repo = new FakeRepo();
        var service = new VehicleService(repo);

        var results = service.search("Toyota", null, 2020);

        assertFalse(results.isEmpty());
        assertEquals("Toyota", repo.lastMake.get());
        assertNull(repo.lastModel.get());
        assertEquals(2020, repo.lastYear.get());
    }
}