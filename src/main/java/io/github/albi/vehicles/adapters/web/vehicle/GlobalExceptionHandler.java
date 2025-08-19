package io.github.albi.vehicles.adapters.web.vehicle;

import io.github.albi.vehicles.domain.vehicle.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolationException;
import java.util.*;

@RestControllerAdvice
class GlobalExceptionHandler {

    // --- 404---
    @ExceptionHandler(VehicleNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(VehicleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NOT_FOUND", ex.getMessage()));
    }

    // --- 400: @Valid body errors ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        var msg = String.join("; ",
                fieldErrors.stream().map(f -> f.field() + ": " + f.message()).toList());

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("VALIDATION_ERROR", msg, fieldErrors));
    }

    // --- 400: @RequestParam / @PathVariable validation (@Validated on controller if needed) ---
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> handleConstraintViolations(ConstraintViolationException ex) {
        var fieldErrors = ex.getConstraintViolations().stream()
                .map(v -> new FieldError(
                        // propertyPath like "create.req.modelYear" → take last node
                        Optional.ofNullable(v.getPropertyPath()).map(p -> {
                            var it = p.iterator(); String last = "";
                            while (it.hasNext()) last = it.next().getName();
                            return last == null ? "" : last;
                        }).orElse(""),
                        v.getMessage()))
                .toList();

        var msg = String.join("; ",
                fieldErrors.stream().map(f -> f.field() + ": " + f.message()).toList());

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("VALIDATION_ERROR", msg, fieldErrors));
    }

    // --- 400: wrong types / missing params ---
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String field = ex.getName();
        String msg = "Invalid value for '" + field + "'";
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("INVALID_ARGUMENT", msg,
                        List.of(new FieldError(field, msg))));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String field = ex.getParameterName();
        String msg = "Missing required parameter '" + field + "'";
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("INVALID_ARGUMENT", msg,
                        List.of(new FieldError(field, msg))));
    }

    // --- 409: database uniqueness / FK issues etc. with field mapping ---
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();
        String friendly = "Request violates data constraints";

        Throwable root = unwrap(ex);

        // Case 1: Hibernate's ConstraintViolationException with constraint name
        if (root instanceof org.hibernate.exception.ConstraintViolationException hce) {
            String c = safe(hce.getConstraintName());
            FieldError mapped = mapConstraintToField(c);
            if (mapped != null) fieldErrors.add(mapped);
            friendly = messageForConstraint(c);
        }
        // Case 2: Plain JDBC SQLException (fallback heuristics)
        else if (root instanceof java.sql.SQLException sqlEx) {
            String sqlState = safe(sqlEx.getSQLState());
            String msg = safe(sqlEx.getMessage());
            // 23505: unique violation in Postgres/H2
            if ("23505".equals(sqlState) || msg.toLowerCase().contains("unique")) {
                FieldError mapped = inferFieldFromMessage(msg);
                if (mapped != null) fieldErrors.add(mapped);
                friendly = "Duplicate value for a unique field";
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("CONFLICT", friendly, fieldErrors));
    }



    // --- 500: fallback ---
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL", "Unexpected error"));
    }

    // --- tiny DTOs ---
    record ErrorResponse(String code, String message, List<FieldError> fieldErrors) {
        static ErrorResponse of(String code, String message) {
            return new ErrorResponse(code, message, List.of());
        }
        static ErrorResponse of(String code, String message, List<FieldError> fieldErrors) {
            return new ErrorResponse(code, message, fieldErrors == null ? List.of() : fieldErrors);
        }
    }
    record FieldError(String field, String message) {}
    private static Throwable unwrap(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) cur = cur.getCause();
        return cur;
    }

    private static String safe(String s) { return s == null ? "" : s; }

    /** Map known constraint names -> field */
    private static FieldError mapConstraintToField(String constraintName) {
        if (constraintName == null) return null;
        String c = constraintName.toLowerCase();

        // Match your @Table(uniqueConstraints=...) names in VehicleEntity
        if (c.contains("uk_vehicles_vin")) {
            return new FieldError("vin", "must be unique");
        }
        if (c.contains("uk_vehicles_registration")) {
            return new FieldError("registrationNumber", "must be unique");
        }
        // Unknown constraint: optional generic message
        return new FieldError("unknown", "violates constraint: " + constraintName);
    }

    /** Friendly top-level message */
    private static String messageForConstraint(String constraintName) {
        String c = safe(constraintName).toLowerCase();
        if (c.contains("uk_vehicles_vin")) return "VIN already exists";
        if (c.contains("uk_vehicles_registration")) return "Registration number already exists";
        return "Request violates data constraints";
    }

    /** Fallback: infer field from DB error message text if we lack a name */
    private static FieldError inferFieldFromMessage(String dbMessage) {
        String m = safe(dbMessage).toLowerCase();
        if (m.contains("vin") || m.contains("uk_vehicles_vin")) {
            return new FieldError("vin", "must be unique");
        }
        if (m.contains("registration") || m.contains("uk_vehicles_registration")) {
            return new FieldError("registrationNumber", "must be unique");
        }
        return null;
    }


}