package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.domain.vehicle.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(VehicleNotFoundException.class)
    ResponseEntity<String> handleNotFound(VehicleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        org.springframework.validation.FieldError::getField,
                        java.util.stream.Collectors.mapping(org.springframework.validation.FieldError::getDefaultMessage, java.util.stream.Collectors.toList())
                ));
        return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "validation_failed",
                "fields", errors
        ));
    }



}