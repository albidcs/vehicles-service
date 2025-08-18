package io.github.albi.vehicles.adapters.web.vehicle.dto;

import io.github.albi.vehicles.domain.vehicle.FuelType;
import io.github.albi.vehicles.domain.vehicle.VehicleType;

public record VehicleResponse(
        Long id,
        String vin,
        VehicleType type,
        String make,
        String model,
        int modelYear,
        FuelType fuelType,
        String color,
        String registrationNumber
) {}