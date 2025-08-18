package io.github.albi.vehicles.domain.vehicle;

import java.util.Objects;

public final class Vehicle {
    private final VehicleId id;
    private final Vin vin;
    private final VehicleType type;
    private final String make;
    private final String model;
    private final int year;
    private final FuelType fuelType;
    private final String color;               // optional
    private final String registrationNumber;  // optional (plate)

    public Vehicle(
            VehicleId id,
            Vin vin,
            VehicleType type,
            String make,
            String model,
            int year,
            FuelType fuelType,
            String color,
            String registrationNumber
    ) {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (vin == null) throw new IllegalArgumentException("vin is required");
        if (type == null) throw new IllegalArgumentException("type is required");
        if (make == null || make.isBlank()) throw new IllegalArgumentException("make is required");
        if (model == null || model.isBlank()) throw new IllegalArgumentException("model is required");
        if (year < 1886) throw new IllegalArgumentException("year invalid");
        if (fuelType == null) throw new IllegalArgumentException("fuelType is required");

        this.id = id;
        this.vin = vin;
        this.type = type;
        this.make = make;
        this.model = model;
        this.year = year;
        this.fuelType = fuelType;
        this.color = (color == null || color.isBlank()) ? null : color.trim();
        this.registrationNumber = (registrationNumber == null || registrationNumber.isBlank())
                ? null
                : registrationNumber.trim().toUpperCase();
    }

    public VehicleId id() { return id; }
    public Vin vin() { return vin; }
    public VehicleType type() { return type; }
    public String make() { return make; }
    public String model() { return model; }
    public int year() { return year; }
    public FuelType fuelType() { return fuelType; }
    public String color() { return color; }
    public String registrationNumber() { return registrationNumber; }
}