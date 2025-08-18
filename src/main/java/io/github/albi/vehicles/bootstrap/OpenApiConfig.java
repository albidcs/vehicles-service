package io.github.albi.vehicles.bootstrap;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI vehiclesApi() {
        return new OpenAPI().info(new Info()
                .title("Vehicles API")
                .version("v1")
                .description("Simple CRUD/search for vehicles"));
    }
}