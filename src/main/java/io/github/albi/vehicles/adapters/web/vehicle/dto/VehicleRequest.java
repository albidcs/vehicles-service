package io.github.albi.vehicles.adapters.web.vehicle.dto;

import io.github.albi.vehicles.domain.vehicle.FuelType;
import io.github.albi.vehicles.domain.vehicle.VehicleType;
import jakarta.validation.constraints.*;

public record VehicleRequest(
        @NotBlank @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$") String vin,
        @NotNull VehicleType type,
        @NotBlank String make,
        @NotBlank String model,
        @NotNull @Min(1886) Integer year,
        @NotNull FuelType fuelType,
        @Size(max = 40) String color,
        @Size(max = 20) String registrationNumber
) {}