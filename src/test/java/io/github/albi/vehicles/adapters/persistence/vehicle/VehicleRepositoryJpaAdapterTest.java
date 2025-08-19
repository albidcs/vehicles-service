package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.*;
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
        var saved = jpa.save(new VehicleEntity(
                null,
                "1HGCM82633A004352",
                "CAR",
                "Toyota",
                "Yaris",
                2022,
                "PETROL",
                "Blue",
                "ABC123"
        ));

        Optional<Vehicle> result = adapter.findById(new VehicleId(saved.getId()));

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
    @DisplayName("persisted model/modelYear are read back correctly (model_year column)")
    void persistAndReadBack_correctMapping() {
        var saved = jpa.save(new VehicleEntity(
                null, "5YJSA1E26HF000001", "CAR",
                "Ford", "Focus", 2017, "PETROL", "Red", "FOC001"
        ));
        var v = adapter.findById(new VehicleId(saved.getId())).orElseThrow();
        assertThat(v.model()).isEqualTo("Focus");
        assertThat(v.year()).isEqualTo(2017);
    }

    @Test
    @DisplayName("search returns vehicles matching filters")
    void search_returnsMatchingVehicles() {
        jpa.save(new VehicleEntity(null, "33333333333333333", "CAR", "Toyota", "Yaris", 2022, "PETROL", "Black", "TYR001"));
        jpa.save(new VehicleEntity(null, "44444444444444444", "CAR", "Honda",  "Civic", 2020, "DIESEL", "White", "HCV001"));

        var result = adapter.search("Toyota", "Yaris", 2022, VehicleType.CAR, FuelType.PETROL, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().model()).isEqualTo("Yaris");
    }

    @Test
    @DisplayName("create persists and returns generated id")
    void create_persistsAndReturnsId() {
        var created = adapter.create(
                new Vin("WDBUF56X48B123456"),
                VehicleType.CAR,
                "Ford", "Fiesta", 2019,
                FuelType.PETROL,
                "Yellow",
                "FIE123"
        );
        assertThat(created.id().value()).isNotNull();

        var roundTrip = adapter.findById(created.id());
        assertThat(roundTrip).isPresent();
        assertThat(roundTrip.get().model()).isEqualTo("Fiesta");
        assertThat(roundTrip.get().year()).isEqualTo(2019);
    }

    @Test
    @DisplayName("update modifies existing row and returns updated domain object")
    void update_updatesExisting() {
        var saved = jpa.save(new VehicleEntity(null, "7FAYM1EE7KN000001", "CAR", "Honda", "Civic", 2020, "PETROL", "Silver", "CIV100"));
        var id = new VehicleId(saved.getId());

        var updated = adapter.update(
                id,
                new Vin("7FAYM1EE7KN000001"),
                VehicleType.CAR,
                "Honda", "Civic", 2024,
                FuelType.PETROL,
                "Silver",
                "CIV100"
        );

        assertThat(updated.id().value()).isEqualTo(saved.getId());
        assertThat(updated.year()).isEqualTo(2024);

        var db = jpa.findById(saved.getId()).orElseThrow();
        assertThat(db.getModelYear()).isEqualTo(2024);
    }

    @Test
    @DisplayName("delete removes row; subsequent findById returns empty")
    void delete_removesRow() {
        var saved = jpa.save(new VehicleEntity(null, "1HGFA16506L000001", "CAR", "Toyota", "Yaris", 2022, "PETROL", "Blue", "TYR777"));
        var id = new VehicleId(saved.getId());

        adapter.delete(id);

        assertThat(adapter.findById(id)).isEmpty();
        assertThat(jpa.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("search is case-insensitive for make and model")
    void search_caseInsensitive() {
        jpa.save(new VehicleEntity(null, "88888888888888888", "CAR", "TOYOTA", "YARIS", 2022, "PETROL", "Black", "TYY888"));
        var result = adapter.search("toyota", "yaris", 2022, VehicleType.CAR, FuelType.PETROL, null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("search ignores null or blank filters (no constraint applied)")
    void search_ignoresNullOrBlank() {
        jpa.save(new VehicleEntity(null, "99999999999999999", "CAR", "Tesla", "Model 3", 2023, "ELECTRIC", "White", "TSL303"));
        jpa.save(new VehicleEntity(null, "10101010101010101", "CAR", "BMW",   "i3",      2019, "ELECTRIC", "Gray",  "BMWi300"));

        // blank model should be ignored; only modelYear constrains
        var r1 = adapter.search(" ", null, 2023, null, null, null, null);
        assertThat(r1).extracting(Vehicle::make).containsExactly("Tesla");

        // all blank/null -> no constraints (returns all)
        var r2 = adapter.search(" ", "  ", null, null, null, null, null);
        assertThat(r2).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("search works with any subset of filters")
    void search_subsetOfFilters() {
        jpa.save(new VehicleEntity(null, "12121212121212121", "CAR", "VW", "Golf", 2018, "PETROL", "Blue",  "VWG018"));
        jpa.save(new VehicleEntity(null, "13131313131313131", "CAR", "VW", "Golf", 2020, "PETROL", "Black", "VWG020"));

        assertThat(adapter.search("VW",   null, null, null, null, null, null)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(adapter.search(null, "Golf", null, null, null, null, null)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(adapter.search(null,  null, 2018, null, null, null, null)).extracting(Vehicle::year).containsExactly(2018);
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