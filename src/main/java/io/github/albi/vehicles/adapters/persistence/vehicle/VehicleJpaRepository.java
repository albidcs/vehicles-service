package io.github.albi.vehicles.adapters.persistence.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface VehicleJpaRepository
        extends JpaRepository<VehicleEntity, Long>, JpaSpecificationExecutor<VehicleEntity> {

    Optional<VehicleEntity> findByVin(String vin);
    Optional<VehicleEntity> findByRegistrationNumberIgnoreCase(String registrationNumber);

}