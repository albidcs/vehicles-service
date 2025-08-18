package io.github.albi.vehicles.domain.vehicle;

import java.util.Objects;

/** ISO-3779 VIN (17 chars, excludes I,O,Q). Normalized to uppercase. */
public class Vin {
    private static final String PATTERN = "^[A-HJ-NPR-Z0-9]{17}$";

    private final String value;

    public Vin(String value) {
        Objects.requireNonNull(value, "vin is required");
        var v = value.trim().toUpperCase();
        if (!v.matches(PATTERN)) {
            throw new IllegalArgumentException("Invalid VIN: must be 17 chars A-HJ-NPR-Z0-9");
        }
        this.value = v;
    }

    public String value() { return value; }

    @Override public String toString() { return value; }
    @Override public boolean equals(Object o) { return (o instanceof Vin vin) && value.equals(vin.value); }
    @Override public int hashCode() { return value.hashCode(); }
}
