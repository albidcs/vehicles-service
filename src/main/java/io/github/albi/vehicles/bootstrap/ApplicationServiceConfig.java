package io.github.albi.vehicles.bootstrap;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.VehicleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ApplicationServiceConfig {
    @Bean
    VehicleService vehicleService(VehicleRepository repo) {
        return new VehicleService(repo);
    }


}