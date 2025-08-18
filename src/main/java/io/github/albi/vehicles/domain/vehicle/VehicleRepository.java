package io.github.albi.vehicles.domain.vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Optional<Vehicle> findById(VehicleId id);
    List<Vehicle> search(String make, String model, Integer year);

    Vehicle create(String make, String model, Integer year);
}