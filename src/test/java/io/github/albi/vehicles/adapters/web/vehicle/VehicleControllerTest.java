package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.*;
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

// ...imports unchanged...

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleController")
class VehicleControllerTest {

    private static final String BASE = "/vehicles";

    @Mock private VehicleService vehicleService;
    private MockMvc mvc;

    private static Vehicle sampleVehicle(long id, int year) {
        return new Vehicle(
                new VehicleId(id),
                new Vin("WDB11111111111111"),
                VehicleType.CAR,
                "Toyota",
                "Yaris",
                year,
                FuelType.PETROL,
                "Blue",
                "ABC123"
        );
    }

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
            var v = sampleVehicle(1L, 2022);
            when(vehicleService.getById(new VehicleId(1L))).thenReturn(v);

            mvc.perform(get(BASE + "/{id}", 1).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.vin").value("WDB11111111111111"))
                    .andExpect(jsonPath("$.type").value("CAR"))
                    .andExpect(jsonPath("$.make").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Yaris"))
                    .andExpect(jsonPath("$.modelYear").value(2022))
                    .andExpect(jsonPath("$.fuelType").value("PETROL"))
                    .andExpect(jsonPath("$.color").value("Blue"))
                    .andExpect(jsonPath("$.registrationNumber").value("ABC123"));

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
            var v1 = sampleVehicle(1L, 2022);

            // ✅ stub the 7-arg signature, with vin & registrationNumber = null
            when(vehicleService.search(
                    eq("Toyota"), eq("Yaris"), eq(2022),
                    eq(VehicleType.CAR), eq(FuelType.PETROL),
                    isNull(), isNull())
            ).thenReturn(java.util.List.of(v1));

            mvc.perform(get(BASE)
                            .param("make", "Toyota")
                            .param("model", "Yaris")
                            // ✅ controller expects 'year' (not 'modelYear') as query param
                            .param("year", "2022")
                            .param("type", "CAR")
                            .param("fuelType", "PETROL")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].make").value("Toyota"))
                    .andExpect(jsonPath("$[0].model").value("Yaris"))
                    .andExpect(jsonPath("$[0].modelYear").value(2022))
                    .andExpect(jsonPath("$[0].type").value("CAR"))
                    .andExpect(jsonPath("$[0].fuelType").value("PETROL"));
        }
    }

    @Nested
    @DisplayName("POST /vehicles (create)")
    class Create {
        @Test
        @DisplayName("returns 201, Location header and payload")
        void ok() throws Exception {
            var created = sampleVehicle(42L, 2022);
            when(vehicleService.create(
                    eq(new Vin("WDB11111111111111")),
                    eq(VehicleType.CAR),
                    eq("Toyota"),
                    eq("Yaris"),
                    eq(2022),
                    eq(FuelType.PETROL),
                    eq("Blue"),
                    eq("ABC123")
            )).thenReturn(created);

            mvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                        {
                          "vin": "WDB11111111111111",
                          "type": "CAR",
                          "make": "Toyota",
                          "model": "Yaris",
                          "modelYear": 2022,
                          "fuelType": "PETROL",
                          "color": "Blue",
                          "registrationNumber": "ABC123"
                        }
                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", endsWith("/vehicles/42")))
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.vin").value("WDB11111111111111"));
        }
    }

    @Nested
    @DisplayName("PUT /vehicles/{id} (update)")
    class Update {
        @Test
        @DisplayName("returns 200 and updated payload")
        void ok() throws Exception {
            var updated = sampleVehicle(42L, 2023);
            when(vehicleService.update(
                    eq(new VehicleId(42L)),
                    eq(new Vin("WDB11111111111111")),
                    eq(VehicleType.CAR),
                    eq("Toyota"),
                    eq("Yaris"),
                    eq(2023),
                    eq(FuelType.PETROL),
                    eq("Blue"),
                    eq("ABC123")
            )).thenReturn(updated);

            mvc.perform(put(BASE + "/{id}", 42)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                        {
                          "vin": "WDB11111111111111",
                          "type": "CAR",
                          "make": "Toyota",
                          "model": "Yaris",
                          "modelYear": 2023,
                          "fuelType": "PETROL",
                          "color": "Blue",
                          "registrationNumber": "ABC123"
                        }
                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.modelYear").value(2023));
        }
    }
}