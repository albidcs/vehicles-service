package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.adapters.web.vehicle.dto.VehicleRequest;
import io.github.albi.vehicles.adapters.web.vehicle.dto.VehicleResponse;
import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @Operation(summary = "Get vehicle by id")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> get(@PathVariable long id) {
        var v = service.getById(new VehicleId(id));
        return ResponseEntity.ok(new VehicleResponse(v.id().value(), v.make(), v.model(), v.year()));
    }

    @Operation(summary = "Search vehicles by optional filters")
    @GetMapping
    public List<VehicleResponse> search(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year
    ) {
        return service.search(make, model, year).stream()
                .map(v -> new VehicleResponse(v.id().value(), v.make(), v.model(), v.year()))
                .toList();
    }

    @Operation(summary = "Create a new vehicle")
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest req) {
        var created = service.create(req.make(), req.model(), req.year());

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id().value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new VehicleResponse(created.id().value(), created.make(), created.model(), created.year()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vehicle by id")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable long id,
            @Valid @RequestBody VehicleRequest req
    ) {
        var updated = service.update(new VehicleId(id), req.make(), req.model(), req.year());
        return ResponseEntity.ok(new VehicleResponse(
                updated.id().value(), updated.make(), updated.model(), updated.year()
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle by id")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(new VehicleId(id));
        return ResponseEntity.noContent().build();
    }
}