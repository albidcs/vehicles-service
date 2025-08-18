package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import io.github.albi.vehicles.domain.vehicle.VehicleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleController")
class VehicleControllerTest {

    private static final String BASE = "/vehicles";

    @Mock private VehicleService vehicleService;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(new VehicleController(vehicleService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /vehicles/{id}")
    class GetById {
        @Test
        @DisplayName("returns 200 and vehicle payload")
        void ok() throws Exception {
            var v = new Vehicle(new VehicleId(1L), "Toyota", "Yaris", 2022);
            when(vehicleService.getById(new VehicleId(1L))).thenReturn(v);

            mvc.perform(get(BASE + "/{id}", 1).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.make").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Yaris"))
                    .andExpect(jsonPath("$.modelYear").value(2022));

            verify(vehicleService).getById(new VehicleId(1L));
            verifyNoMoreInteractions(vehicleService);
        }

        @Test
        @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(vehicleService.getById(new VehicleId(999L)))
                    .thenThrow(new VehicleNotFoundException(new VehicleId(999L)));

            mvc.perform(get(BASE + "/{id}", 999).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(vehicleService).getById(new VehicleId(999L));
            verifyNoMoreInteractions(vehicleService);
        }
    }

    @Nested
    @DisplayName("GET /vehicles (search)")
    class Search {
        @Test
        @DisplayName("returns 200 and filtered list")
        void ok() throws Exception {
            var v1 = new Vehicle(new VehicleId(1L), "Toyota", "Yaris", 2022);
            when(vehicleService.search(eq("Toyota"), eq("Yaris"), eq(2022)))
                    .thenReturn(java.util.List.of(v1));

            mvc.perform(get(BASE)
                            .param("make", "Toyota")
                            .param("model", "Yaris")
                            .param("year", "2022")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].make").value("Toyota"))
                    .andExpect(jsonPath("$[0].model").value("Yaris"))
                    .andExpect(jsonPath("$[0].modelYear").value(2022));

            verify(vehicleService).search("Toyota", "Yaris", 2022);
            verifyNoMoreInteractions(vehicleService);
        }
    }

    @Nested
    @DisplayName("POST /vehicles (create)")
    class Create {
        @Test
        @DisplayName("returns 201, Location header and payload")
        void ok() throws Exception {
            var created = new Vehicle(new VehicleId(42L), "Toyota", "Yaris", 2022);
            when(vehicleService.create(eq("Toyota"), eq("Yaris"), eq(2022))).thenReturn(created);

            mvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("""
                        {"make":"Toyota","model":"Yaris","year":2022}
                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", endsWith("/vehicles/42")))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.make").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Yaris"))
                    .andExpect(jsonPath("$.modelYear").value(2022));

            verify(vehicleService).create("Toyota", "Yaris", 2022);
            verifyNoMoreInteractions(vehicleService);
        }

        @Test
        @DisplayName("returns 400 on bean validation errors")
        void badRequest() throws Exception {
            mvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("""
                        {"make":"","model":"","year":1800}
                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(vehicleService);
        }
    }

    @Nested
    @DisplayName("PUT /vehicles/{id} (update)")
    class Update {
        @Test
        @DisplayName("returns 200 and updated payload")
        void ok() throws Exception {
            var updated = new Vehicle(new VehicleId(42L), "Toyota", "Yaris", 2023);
            when(vehicleService.update(eq(new VehicleId(42L)), eq("Toyota"), eq("Yaris"), eq(2023)))
                    .thenReturn(updated);

            mvc.perform(put(BASE + "/{id}", 42)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("""
                        {"make":"Toyota","model":"Yaris","year":2023}
                    """))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.modelYear").value(2023));

            verify(vehicleService).update(new VehicleId(42L), "Toyota", "Yaris", 2023);
            verifyNoMoreInteractions(vehicleService);
        }

        @Test
        @DisplayName("returns 404 when target does not exist")
        void notFound() throws Exception {
            when(vehicleService.update(eq(new VehicleId(999L)), eq("Toyota"), eq("Yaris"), eq(2023)))
                    .thenThrow(new VehicleNotFoundException(new VehicleId(999L)));

            mvc.perform(put(BASE + "/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("""
                        {"make":"Toyota","model":"Yaris","year":2023}
                    """))
                    .andExpect(status().isNotFound());

            verify(vehicleService).update(new VehicleId(999L), "Toyota", "Yaris", 2023);
            verifyNoMoreInteractions(vehicleService);
        }
    }

    @Nested
    @DisplayName("DELETE /vehicles/{id}")
    class Delete {
        @Test
        @DisplayName("returns 204 on success")
        void ok() throws Exception {
            doNothing().when(vehicleService).delete(new VehicleId(42L));

            mvc.perform(delete(BASE + "/{id}", 42).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(vehicleService).delete(new VehicleId(42L));
            verifyNoMoreInteractions(vehicleService);
        }

        @Test
        @DisplayName("returns 404 when target does not exist")
        void notFound() throws Exception {
            doThrow(new VehicleNotFoundException(new VehicleId(999L)))
                    .when(vehicleService).delete(new VehicleId(999L));

            mvc.perform(delete(BASE + "/{id}", 999).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(vehicleService).delete(new VehicleId(999L));
            verifyNoMoreInteractions(vehicleService);
        }
    }
}