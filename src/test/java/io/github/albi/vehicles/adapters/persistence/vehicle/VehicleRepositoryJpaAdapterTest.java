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


    @Test
    @DisplayName("findById returns empty when vehicle does not exist")
    void findById_whenMissing_returnsEmpty() {
        assertThat(adapter.findById(new VehicleId(999L))).isEmpty();
    }

    @Test
    @DisplayName("persisted model/year are read back correctly (model_year column)")
    void persistAndReadBack_correctMapping() {
        var saved = jpa.save(new VehicleEntity(null, "Ford", "Focus", 2017));
        var v = adapter.findById(new VehicleId(saved.getId())).orElseThrow();
        assertThat(v.model()).isEqualTo("Focus");
        assertThat(v.year()).isEqualTo(2017);
    }

    @Test
    @DisplayName("search returns vehicles matching filters")
    void search_returnsMatchingVehicles() {
        jpa.save(new VehicleEntity(null, "Toyota", "Yaris", 2022));
        jpa.save(new VehicleEntity(null, "Honda", "Civic", 2020));

        var result = adapter.search("Toyota", "Yaris", 2022);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().model()).isEqualTo("Yaris");
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