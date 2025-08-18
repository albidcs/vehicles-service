package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock private VehicleService vehicleService;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(new VehicleController(vehicleService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getById_ok() throws Exception {
        var v = new Vehicle(new VehicleId(1L), "Toyota", "Yaris", 2022);
        Mockito.when(vehicleService.getById(new VehicleId(1L))).thenReturn(v);

        mvc.perform(get("/vehicles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Yaris"))
                .andExpect(jsonPath("$.modelYear").value(2022));
    }

    @Test
    void search_ok() throws Exception {
        var v1 = new Vehicle(new VehicleId(1L), "Toyota", "Yaris", 2022);
        Mockito.when(vehicleService.search(eq("Toyota"), eq("Yaris"), eq(2022)))
                .thenReturn(List.of(v1));

        mvc.perform(get("/vehicles")
                        .param("make", "Toyota")
                        .param("model", "Yaris")
                        .param("year", "2022"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Yaris"))
                .andExpect(jsonPath("$[0].modelYear").value(2022));
    }

    @Test
    void create_ok() throws Exception {
        var created = new Vehicle(new VehicleId(42L), "Toyota", "Yaris", 2022);
        Mockito.when(vehicleService.create(eq("Toyota"), eq("Yaris"), eq(2022)))
                .thenReturn(created);

        mvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                         {"make":"Toyota","model":"Yaris","year":2022}
                         """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/vehicles/42")))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Yaris"))
                .andExpect(jsonPath("$.modelYear").value(2022));
    }

    @Test
    void update_ok() throws Exception {
        var updated = new Vehicle(new VehicleId(42L), "Toyota", "Yaris", 2023);
        Mockito.when(vehicleService.update(eq(new VehicleId(42L)), eq("Toyota"), eq("Yaris"), eq(2023)))
                .thenReturn(updated);

        mvc.perform(put("/vehicles/{id}", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"make":"Toyota","model":"Yaris","year":2023}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Yaris"))
                .andExpect(jsonPath("$.modelYear").value(2023));
    }

    @Test
    void delete_ok() throws Exception {
        // just verify 204 comes back, no body expected
        mvc.perform(delete("/vehicles/{id}", 42))
                .andExpect(status().isNoContent());

        Mockito.verify(vehicleService).delete(new VehicleId(42L));
    }

    @Test
    void create_badRequest() throws Exception {
        // make/model blank, year too small -> bean validation should trigger 400
        mvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                         {"make":"","model":"","year":1800}
                         """))
                .andExpect(status().isBadRequest());
    }
}