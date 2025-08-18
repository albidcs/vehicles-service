package io.github.albi.vehicles.domain.vehicle;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

final class VehicleServiceTest {

    // Minimal fake repository to assert contract without Spring or a DB.
    private static final class FakeRepo implements VehicleRepository {

        private final Set<Long> deleted = new HashSet<>();

        final AtomicReference<VehicleId> lastFindById = new AtomicReference<>();
        final AtomicReference<String> lastMake = new AtomicReference<>();
        final AtomicReference<String> lastModel = new AtomicReference<>();
        final AtomicReference<Integer> lastYear = new AtomicReference<>();
        final AtomicReference<VehicleId> lastUpdatedId = new AtomicReference<>();
        final AtomicReference<String>   lastUpdatedMake = new AtomicReference<>();
        final AtomicReference<String>   lastUpdatedModel = new AtomicReference<>();
        final AtomicReference<Integer>  lastUpdatedYear = new AtomicReference<>();
        final AtomicReference<VehicleId> lastDeletedId = new AtomicReference<>();

        @Override
        public Optional<Vehicle> findById(VehicleId id) {
            lastFindById.set(id);
            if (deleted.contains(id.value())) return Optional.empty();
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

        @Override
        public Vehicle update(VehicleId id, String make, String model, Integer year) {
            lastUpdatedId.set(id);
            lastUpdatedMake.set(make);
            lastUpdatedModel.set(model);
            lastUpdatedYear.set(year);
            return new Vehicle(id, make, model, year);
        }

        @Override
        public void delete(VehicleId id) {
            lastDeletedId.set(id);
            deleted.add(id.value());
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

    @Test
    void update_forwardsArgs_andReturnsUpdatedVehicle() {
        var repo = new FakeRepo();
        var service = new VehicleService(repo);

        var id = new VehicleId(1L);
        var updated = service.update(id, "Honda", "Civic", 2024);

        assertEquals(id, repo.lastUpdatedId.get());
        assertEquals("Honda", repo.lastUpdatedMake.get());
        assertEquals("Civic", repo.lastUpdatedModel.get());
        assertEquals(2024, repo.lastUpdatedYear.get());

        assertEquals(1L, updated.id().value());
        assertEquals("Honda", updated.make());
        assertEquals("Civic", updated.model());
        assertEquals(2024, updated.year());
    }

    @Test
    void delete_marksEntityDeleted_andSubsequentGetThrows() {
        var repo = new FakeRepo();
        var service = new VehicleService(repo);

        var id = new VehicleId(1L);
        service.delete(id);

        assertEquals(id, repo.lastDeletedId.get());
        assertThrows(VehicleNotFoundException.class, () -> service.getById(id));
    }


}