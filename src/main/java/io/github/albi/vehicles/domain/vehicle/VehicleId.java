package io.github.albi.vehicles.domain.vehicle;


import java.util.Objects;


/**
 * Value object representing the unique identifier of a Vehicle in the domain model.
 *
 * Used to ensure strong type safety and prevent accidental mix-ups with other ID types.
 * Encapsulates validation logic for ID creation.
 */
public final class VehicleId {

    // Strongly typed ID to avoid primitive obsession
    private final Long value;

    public VehicleId(Long value) {
        if (value == null || value <= 0) throw new IllegalArgumentException("id must be positive");
        this.value = value;
    }
    public Long value() { return value; }

    @Override public boolean equals(Object o){ return (o instanceof VehicleId v) && Objects.equals(value, v.value); }
    @Override public int hashCode(){ return Objects.hash(value); }
    @Override public String toString(){ return String.valueOf(value); }
}