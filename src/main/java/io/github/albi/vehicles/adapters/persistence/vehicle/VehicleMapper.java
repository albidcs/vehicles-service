package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.*;

final class VehicleMapper {

    private VehicleMapper() {}

    static Vehicle toDomain(VehicleEntity e) {
        Long rawId = e.getId();
        if (rawId == null) {
            // Aligns with test expectation
            throw new IllegalStateException("VehicleEntity id must not be null");
        }

        return new Vehicle(
                new VehicleId(rawId),                                            // safe after null-check
                e.getVin() != null ? new Vin(e.getVin()) : null,
                e.getType() != null ? VehicleType.valueOf(e.getType()) : null,
                e.getMake(),
                e.getModel(),
                e.getModelYear(),
                e.getFuelType() != null ? FuelType.valueOf(e.getFuelType()) : null,
                e.getColor(),
                e.getRegistrationNumber()
        );
    }

    static VehicleEntity toEntity(Vehicle v) {
        return new VehicleEntity(
                v.id() != null ? v.id().value() : null,
                v.vin() != null ? v.vin().value() : null,
                v.type() != null ? v.type().name() : null,
                v.make(),
                v.model(),
                v.year(),
                v.fuelType() != null ? v.fuelType().name() : null,
                v.color(),
                v.registrationNumber()
        );
    }
}