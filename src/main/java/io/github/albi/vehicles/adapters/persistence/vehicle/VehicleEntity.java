package io.github.albi.vehicles.adapters.persistence.vehicle;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicles_make", columnList = "make"),
                @Index(name = "idx_vehicles_model", columnList = "model"),
                @Index(name = "idx_vehicles_year", columnList = "year")
        })
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String make;

    @Column(name="model_year",nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private int year;

    protected VehicleEntity() {} // JPA

    public VehicleEntity(Long id, String make, String model, int year) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public Long getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getModelYear() { return year; }
}