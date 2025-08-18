package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.domain.vehicle.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(VehicleNotFoundException.class)
    ResponseEntity<String> handleNotFound(VehicleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}