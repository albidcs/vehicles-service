// VehicleService.java
package io.github.albi.vehicles.application.vehicle;

import io.github.albi.vehicles.domain.vehicle.*;
import java.util.List;
import java.util.Objects;

public final class VehicleService {
    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Vehicle getById(VehicleId id) {
        return repository.findById(id).orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public List<Vehicle> search(String make, String model, Integer year,
                                VehicleType type, FuelType fuelType,
                                String vin, String registrationNumber) {
        return repository.search(make, model, year, type, fuelType, vin, registrationNumber);
    }

    public Vehicle create(Vin vin, VehicleType type, String make, String model, Integer year,
                          FuelType fuelType, String color, String registrationNumber) {
        return repository.create(vin, type, make, model, year, fuelType, color, registrationNumber);
    }

    public Vehicle update(VehicleId id, Vin vin, VehicleType type, String make, String model, Integer year,
                          FuelType fuelType, String color, String registrationNumber) {
        getById(id); // keep 404 semantics consistent
        return repository.update(id, vin, type, make, model, year, fuelType, color, registrationNumber);
    }

    public void delete(VehicleId id) {
        getById(id);
        repository.delete(id);
    }
}