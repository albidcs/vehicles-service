package io.github.albi.vehicles.adapters.persistence.vehicle;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicles_make", columnList = "make"),
                @Index(name = "idx_vehicles_model", columnList = "model"),
                @Index(name = "idx_vehicles_year", columnList = "model_year")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicles_vin", columnNames = "vin"),
                @UniqueConstraint(name = "uk_vehicles_registration", columnNames = "registration_number")
        })
public class VehicleEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vin", length = 17, nullable = true) // keep nullable for smoother migration
    private String vin;

    @Column(name = "type", length = 20, nullable = true)
    private String type;

    @Column(name = "make", nullable = false)
    private String make;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "model_year", nullable = false)
    private Integer modelYear;

    @Column(name = "fuel_type", length = 20, nullable = true)
    private String fuelType;

    @Column(name = "color", length = 40)
    private String color;

    @Column(name = "registration_number", length = 20)
    private String registrationNumber;

    protected VehicleEntity() {}

    public VehicleEntity(Long id, String vin, String type, String make, String model, Integer modelYear,
                         String fuelType, String color, String registrationNumber) {
        this.id = id;
        this.vin = vin;
        this.type = type;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.fuelType = fuelType;
        this.color = color;
        this.registrationNumber = registrationNumber;
    }

    public Long getId() { return id; }
    public String getVin() { return vin; }
    public String getType() { return type; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public Integer getModelYear() { return modelYear; }
    public String getFuelType() { return fuelType; }
    public String getColor() { return color; }
    public String getRegistrationNumber() { return registrationNumber; }

    public void setId(Long id) { this.id = id; }
    public void setVin(String vin) { this.vin = vin; }
    public void setType(String type) { this.type = type; }
    public void setMake(String make) { this.make = make; }
    public void setModel(String model) { this.model = model; }
    public void setModelYear(Integer modelYear) { this.modelYear = modelYear; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public void setColor(String color) { this.color = color; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
}