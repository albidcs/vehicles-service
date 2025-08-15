package io.github.albi.vehicles.domain.vehicle;


public final class Vehicle {

    // Domain-level identity, not tied to persistence
    private final VehicleId id;
    private final String make;
    private final String model;
    private final int year;


    public Vehicle(VehicleId id, String make, String model, int year) {
        // Enforce business invariants at the domain boundary
        if (id == null) throw new IllegalArgumentException("id is required");
        if (make == null || make.isBlank()) throw new IllegalArgumentException("make is required");
        if (model == null || model.isBlank()) throw new IllegalArgumentException("model is required");
        if (year < 1886) throw new IllegalArgumentException("year invalid");
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public VehicleId id() { return id; }
    public String make() { return make; }
    public String model() { return model; }
    public int year() { return year; }
}