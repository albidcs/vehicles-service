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

    @Test
    @DisplayName("create persists and returns generated id")
    void create_persistsAndReturnsId() {
        var created = adapter.create("Ford", "Fiesta", 2019);
        assertThat(created.id().value()).isNotNull();
        var roundTrip = adapter.findById(created.id());
        assertThat(roundTrip).isPresent();
        assertThat(roundTrip.get().model()).isEqualTo("Fiesta");
        assertThat(roundTrip.get().year()).isEqualTo(2019);
    }

    @Test
    @DisplayName("update modifies existing row and returns updated domain object")
    void update_updatesExisting() {
        var saved = jpa.save(new VehicleEntity(null, "Honda", "Civic", 2020));
        var id = new VehicleId(saved.getId());

        var updated = adapter.update(id, "Honda", "Civic", 2024);

        assertThat(updated.id().value()).isEqualTo(saved.getId());
        assertThat(updated.year()).isEqualTo(2024);

        var db = jpa.findById(saved.getId()).orElseThrow();
        assertThat(db.getModelYear()).isEqualTo(2024);
    }

    @Test
    @DisplayName("delete removes row; subsequent findById returns empty")
    void delete_removesRow() {
        var saved = jpa.save(new VehicleEntity(null, "Toyota", "Yaris", 2022));
        var id = new VehicleId(saved.getId());

        adapter.delete(id);

        assertThat(adapter.findById(id)).isEmpty();
        assertThat(jpa.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("search is case-insensitive for make and model")
    void search_caseInsensitive() {
        jpa.save(new VehicleEntity(null, "TOYOTA", "YARIS", 2022));
        var result = adapter.search("toyota", "yaris", 2022);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("search ignores null or blank filters (no constraint applied)")
    void search_ignoresNullOrBlank() {
        jpa.save(new VehicleEntity(null, "Tesla", "Model 3", 2023));
        jpa.save(new VehicleEntity(null, "BMW", "i3", 2019));

        // blank model should be ignored; only year constrains
        var r1 = adapter.search(" ", null, 2023);
        assertThat(r1).extracting(Vehicle::make).containsExactly("Tesla");

        // all blank/null -> no constraints (returns all)
        var r2 = adapter.search(" ", "  ", null);
        assertThat(r2).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("search works with any subset of filters")
    void search_subsetOfFilters() {
        jpa.save(new VehicleEntity(null, "VW", "Golf", 2018));
        jpa.save(new VehicleEntity(null, "VW", "Golf", 2020));

        assertThat(adapter.search("VW", null, null)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(adapter.search(null, "Golf", null)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(adapter.search(null, null, 2018)).extracting(Vehicle::year).containsExactly(2018);
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