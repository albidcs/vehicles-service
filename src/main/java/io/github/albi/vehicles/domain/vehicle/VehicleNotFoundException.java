package io.github.albi.vehicles.domain.vehicle;

public class VehicleNotFoundException {


    /**
     * Exception thrown when a requested Vehicle is not found in the domain.
     *
     * This lives in the domain layer, so it can be thrown from business logic
     * without depending on infrastructure or web layers.
     */
    public class VehicleNotFound extends RuntimeException {
        public VehicleNotFound(VehicleId id) {
            super("Vehicle not found: " + (id == null ? "(null)" : id.value()));
        }
    }
}
