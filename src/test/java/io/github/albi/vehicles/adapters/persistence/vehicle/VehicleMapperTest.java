package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class VehicleMapperTest {

    @Test
    @DisplayName("toEntity maps domain -> JPA entity")
    void toEntity_mapsDomainToEntity() {
        var domain = new Vehicle(new VehicleId(42L), "Toyota", "Yaris", 2022);

        var entity = VehicleMapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(42L);
        assertThat(entity.getMake()).isEqualTo("Toyota");
        assertThat(entity.getModel()).isEqualTo("Yaris");
        assertThat(entity.getYear()).isEqualTo(2022);
    }

    @Test
    @DisplayName("toDomain maps JPA entity -> domain")
    void toDomain_mapsEntityToDomain() {
        var entity = new VehicleEntity(7L, "Honda", "Civic", 2020);

        var domain = VehicleMapper.toDomain(entity);

        assertThat(domain.id().value()).isEqualTo(7L);
        assertThat(domain.make()).isEqualTo("Honda");
        assertThat(domain.model()).isEqualTo("Civic");
        assertThat(domain.year()).isEqualTo(2020);
    }

    @Test
    @DisplayName("toDomain throws if entity id is null")
    void toDomain_throwsOnNullId() {
        var entity = new VehicleEntity(null, "BMW", "320i", 2018);

        assertThrows(IllegalStateException.class, () -> VehicleMapper.toDomain(entity));
    }
}