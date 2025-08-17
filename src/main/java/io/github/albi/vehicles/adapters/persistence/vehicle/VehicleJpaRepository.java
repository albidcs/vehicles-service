package io.github.albi.vehicles.adapters.persistence.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface VehicleJpaRepository
        extends JpaRepository<VehicleEntity, Long>, JpaSpecificationExecutor<VehicleEntity> {

}