// VehicleRepository.java
package io.github.albi.vehicles.domain.vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Optional<Vehicle> findById(VehicleId id);

    // All filters are optional; null = ignored
    List<Vehicle> search(String make, String model, Integer year,
                         VehicleType type, FuelType fuelType,
                         String vin, String registrationNumber);

    Vehicle create(Vin vin, VehicleType type, String make, String model, Integer year,
                   FuelType fuelType, String color, String registrationNumber);

    Vehicle update(VehicleId id, Vin vin, VehicleType type, String make, String model, Integer year,
                   FuelType fuelType, String color, String registrationNumber);

    void delete(VehicleId id);
}