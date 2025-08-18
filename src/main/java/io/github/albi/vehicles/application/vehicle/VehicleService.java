package io.github.albi.vehicles.application.vehicle;


import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import io.github.albi.vehicles.domain.vehicle.VehicleNotFoundException;
import io.github.albi.vehicles.domain.vehicle.VehicleRepository;

import java.util.List;
import java.util.Objects;

public final class VehicleService {
    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Vehicle getById(VehicleId id) {
        return repository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public List<Vehicle> search(String make, String model, Integer year) {
        return repository.search(make, model, year);
    }

    public Vehicle create(String make, String model, Integer year) {
        return repository.create(make, model, year);
    }
};