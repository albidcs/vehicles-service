package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.*;


import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;

final class VehicleMapper {
    static Vehicle toDomain(VehicleEntity e) {
        return new Vehicle(
                new VehicleId(e.getId()),
                e.getVin() == null ? new Vin("AAAAAAAAAAAAAAAAA") : new Vin(e.getVin()), // temp safety if legacy row exists
                e.getType() == null ? VehicleType.OTHER : VehicleType.valueOf(e.getType()),
                e.getMake(),
                e.getModel(),
                e.getModelYear(),
                e.getFuelType() == null ? FuelType.OTHER : FuelType.valueOf(e.getFuelType()),
                e.getColor(),
                e.getRegistrationNumber()
        );
    }

    static VehicleEntity toEntity(Vehicle v) {
        return new VehicleEntity(
                v.id().value(),
                v.vin().value(),
                v.type().name(),
                v.make(),
                v.model(),
                v.year(),
                v.fuelType().name(),
                v.color(),
                v.registrationNumber()
        );
    }
}