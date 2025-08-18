package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.adapters.web.vehicle.dto.VehicleRequest;
import io.github.albi.vehicles.adapters.web.vehicle.dto.VehicleResponse;
import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Tag(name = "Vehicles")
@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService service;
    public VehicleController(VehicleService service) { this.service = service; }

    @Operation(summary = "Get vehicle by id")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> get(@PathVariable long id) {
        var v = service.getById(new VehicleId(id));
        return ResponseEntity.ok(toResponse(v));
    }

    @Operation(summary = "Search vehicles")
    @GetMapping
    public List<VehicleResponse> search(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) FuelType fuelType
    ) {
        return service.search(make, model, year, type, fuelType)
                .stream().map(this::toResponse).toList();
    }

    @Operation(summary = "Create vehicle")
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest req) {
        var created = service.create(
                new Vin(req.vin()), req.type(), req.make(), req.model(), req.year(),
                req.fuelType(), req.color(), req.registrationNumber()
        );
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id().value()).toUri();
        return ResponseEntity.created(location).body(toResponse(created));
    }

    @Operation(summary = "Update vehicle")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(@PathVariable long id, @Valid @RequestBody VehicleRequest req) {
        var updated = service.update(
                new VehicleId(id),
                new Vin(req.vin()), req.type(), req.make(), req.model(), req.year(),
                req.fuelType(), req.color(), req.registrationNumber()
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Delete vehicle")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(new VehicleId(id));
        return ResponseEntity.noContent().build();
    }

    private VehicleResponse toResponse(Vehicle v) {
        return new VehicleResponse(
                v.id().value(), v.vin().value(), v.type(), v.make(), v.model(),
                v.year(), v.fuelType(), v.color(), v.registrationNumber()
        );
    }
}