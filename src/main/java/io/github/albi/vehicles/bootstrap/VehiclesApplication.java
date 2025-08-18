package io.github.albi.vehicles.bootstrap;

import io.github.albi.vehicles.adapters.persistence.vehicle.VehicleEntity;
import io.github.albi.vehicles.adapters.persistence.vehicle.VehicleJpaRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = "io.github.albi.vehicles")
@EnableJpaRepositories(basePackageClasses = VehicleJpaRepository.class)
@EntityScan(basePackageClasses = VehicleEntity.class)
public class VehiclesApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehiclesApplication.class, args);
    }

}
