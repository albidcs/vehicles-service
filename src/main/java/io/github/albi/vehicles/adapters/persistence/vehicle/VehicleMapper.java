package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;


import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;

public final class VehicleMapper {
    private VehicleMapper() {}

    public static VehicleEntity toEntity(Vehicle domain) {
        Long id = domain.id() == null ? null : domain.id().value();
        return new VehicleEntity(id, domain.make(), domain.model(), domain.year());
    }

    public static Vehicle toDomain(VehicleEntity entity) {
        Long id = entity.getId();
        if (id == null) {
            throw new IllegalStateException("VehicleEntity id must be non-null to map to domain");
        }
        return new Vehicle(new VehicleId(id), entity.getMake(), entity.getModel(), entity.getModelYear());
    }
}