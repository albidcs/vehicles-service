package io.github.albi.vehicles.adapters.web.vehicle.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(
        @NotBlank String make,
        @NotBlank String model,
        @NotNull @Min(1886) Integer year
) {}