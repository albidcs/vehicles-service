package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class VehicleMapperTest {

    @Test
    @DisplayName("toEntity maps domain -> JPA entity (all fields)")
    void toEntity_mapsDomainToEntity() {
        var domain = new Vehicle(
                new VehicleId(42L),
                new Vin("1HGCM82633A004352"),
                VehicleType.CAR,
                "Toyota",
                "Yaris",
                2022,
                FuelType.PETROL,
                "Blue",
                "ABC123"
        );

        var entity = VehicleMapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(42L);
        assertThat(entity.getVin()).isEqualTo("1HGCM82633A004352");
        assertThat(entity.getType()).isEqualTo("CAR");
        assertThat(entity.getMake()).isEqualTo("Toyota");
        assertThat(entity.getModel()).isEqualTo("Yaris");
        assertThat(entity.getModelYear()).isEqualTo(2022);
        assertThat(entity.getFuelType()).isEqualTo("PETROL");
        assertThat(entity.getColor()).isEqualTo("Blue");
        assertThat(entity.getRegistrationNumber()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("toDomain maps JPA entity -> domain (all fields)")
    void toDomain_mapsEntityToDomain() {
        var entity = new VehicleEntity(
                7L,
                "WDBUF56X48B123456",
                "CAR",
                "Honda",
                "Civic",
                2020,
                "DIESEL",
                "Black",
                "XYZ987"
        );

        var domain = VehicleMapper.toDomain(entity);

        assertThat(domain.id().value()).isEqualTo(7L);
        assertThat(domain.vin().value()).isEqualTo("WDBUF56X48B123456");
        assertThat(domain.type()).isEqualTo(VehicleType.CAR);
        assertThat(domain.make()).isEqualTo("Honda");
        assertThat(domain.model()).isEqualTo("Civic");
        assertThat(domain.year()).isEqualTo(2020);
        assertThat(domain.fuelType()).isEqualTo(FuelType.DIESEL);
        assertThat(domain.color()).isEqualTo("Black");
        assertThat(domain.registrationNumber()).isEqualTo("XYZ987");
    }

    @Test
    @DisplayName("toDomain throws if entity id is null")
    void toDomain_throwsOnNullId() {
        var entity = new VehicleEntity(
                null,
                "5YJSA1E26HF000001",
                "CAR",
                "BMW",
                "320i",
                2018,
                "PETROL",
                "White",
                "BMW320"
        );

        assertThrows(IllegalStateException.class, () -> VehicleMapper.toDomain(entity));
    }
}