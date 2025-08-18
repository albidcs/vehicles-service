package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.adapters.web.vehicle.dto.VehicleResponse;
import io.github.albi.vehicles.application.vehicle.VehicleService;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService service;
    public VehicleController(VehicleService service) { this.service = service; }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> get(@PathVariable long id) {
        var v = service.getById(new VehicleId(id));
        return ResponseEntity.ok(new VehicleResponse(v.id().value(), v.make(), v.model(), v.year()));
    }

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
}