package io.github.albi.vehicles.adapters.persistence.vehicle;

import jakarta.persistence.*;

@Entity
@Table(
        name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicles_make", columnList = "make"),
                @Index(name = "idx_vehicles_model", columnList = "model"),
                // IMPORTANT: index on model_year, not year
                @Index(name = "idx_vehicles_model_year", columnList = "model_year")
        }
)
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String make;

    @Column(length = 100, nullable = false)
    private String model;

    // single source of truth for the year
    @Column(name = "model_year", nullable = false)
    private int modelYear;

    protected VehicleEntity() {
    }

    public VehicleEntity(Long id, String make, String model, int modelYear) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
    }

    public Long getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getModelYear() {
        return modelYear;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }
}