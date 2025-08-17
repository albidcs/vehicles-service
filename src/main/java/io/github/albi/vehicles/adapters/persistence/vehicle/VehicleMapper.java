package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;

public final class VehicleMapper {
    private VehicleMapper() {}

    public static VehicleEntity toEntity(Vehicle domain) {
        Long id = domain.id() == null ? null : domain.id().value();
        return new VehicleEntity(id, domain.make(), domain.model(), domain.year());
    }

    public static Vehicle toDomain(VehicleEntity entity) {
        return new Vehicle(
                entity.getId() == null ? null : new VehicleId(entity.getId()),
                entity.getMake(),
                entity.getModel(),
                entity.getYear()
        );
    }
}