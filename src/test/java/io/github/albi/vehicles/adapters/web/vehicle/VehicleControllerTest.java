package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock private VehicleService vehicleService;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new VehicleController(vehicleService)).build();
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
                        .param("make","Toyota").param("model","Yaris").param("year","2022"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Yaris"))
                .andExpect(jsonPath("$[0].modelYear").value(2022));
    }
}