package io.github.albi.vehicles.adapters.persistence.vehicle;


import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import io.github.albi.vehicles.domain.vehicle.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // picks application-test.yml
@org.springframework.context.annotation.Import(VehicleRepositoryJpaAdapterTest.ScanConfig.class)
class VehicleRepositoryJpaAdapterTest {

    @Autowired private VehicleJpaRepository jpa;
    @Autowired private VehicleRepository adapter;

    @Test
    @DisplayName("findById returns mapped domain object")
    void findById_returnsVehicle() {
        // Arrange – persist via JPA repository
        var saved = jpa.save(new VehicleEntity(null, "Toyota", "Yaris", 2022));

        // Act – fetch through domain adapter
        Optional<Vehicle> result = adapter.findById(new VehicleId(saved.getId()));

        // Assert
        assertThat(result).isPresent();
        var v = result.get();
        assertThat(v.id().value()).isEqualTo(saved.getId());
        assertThat(v.make()).isEqualTo("Toyota");
        assertThat(v.model()).isEqualTo("Yaris");
        assertThat(v.year()).isEqualTo(2022);
    }

    @Configuration
    @EnableJpaRepositories(basePackageClasses = VehicleJpaRepository.class)
    @EntityScan(basePackageClasses = VehicleEntity.class)
    static class ScanConfig {
        @Bean
        VehicleRepository vehicleRepositoryAdapter(VehicleJpaRepository repo) {
            return new VehicleRepositoryJpaAdapter(repo);
        }
    }
}