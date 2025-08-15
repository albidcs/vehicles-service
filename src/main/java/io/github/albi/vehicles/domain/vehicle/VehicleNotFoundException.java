package io.github.albi.vehicles.domain.vehicle;



/**
 * Exception thrown when a requested Vehicle is not found in the domain.
 *
 * <p>This lives in the domain layer, so it can be thrown from business logic
 * without depending on infrastructure or web layers.</p>
 */
public final class VehicleNotFoundException extends RuntimeException {

    /**
     * Creates a new exception for the given VehicleId.
     *
     * @param id the vehicle ID that was not found (nullable for unknown IDs)
     */
    public VehicleNotFoundException(VehicleId id) {
        super("Vehicle not found: " + (id == null ? "(null)" : id.value()));
    }
}
