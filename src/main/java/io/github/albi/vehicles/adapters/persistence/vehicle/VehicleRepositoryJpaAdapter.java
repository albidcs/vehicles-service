package io.github.albi.vehicles.adapters.persistence.vehicle;

import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import io.github.albi.vehicles.domain.vehicle.VehicleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import io.github.albi.vehicles.domain.vehicle.*;

@Repository
public class VehicleRepositoryJpaAdapter implements VehicleRepository {

    private final VehicleJpaRepository jpa;

    public VehicleRepositoryJpaAdapter(VehicleJpaRepository jpa) {
        this.jpa = jpa;
    }


    @Override
    public Optional<Vehicle> findById(VehicleId id) {
        return jpa.findById(id.value()).map(VehicleMapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findByVin(Vin vin) {
        return jpa.findByVin(vin.value()).map(VehicleMapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findByRegistrationNumber(String registrationNumber) {
        return jpa.findByRegistrationNumberIgnoreCase(registrationNumber).map(VehicleMapper::toDomain);
    }

    @Override
    public List<Vehicle> search(String make, String model, Integer year,
                                VehicleType type, FuelType fuelType,
                                String vin, String registrationNumber) {
        Specification<VehicleEntity> spec = andAll(
                likeIgnoreCaseIfPresent("make", make),
                likeIgnoreCaseIfPresent("model", model),
                equalsIfPresent("modelYear", year),
                equalsIfPresent("type", type == null ? null : type.name()),
                equalsIfPresent("fuelType", fuelType == null ? null : fuelType.name())
        );
        return jpa.findAll(spec).stream().map(VehicleMapper::toDomain).toList();
    }

    @Transactional
    @Override
    public Vehicle create(Vin vin, VehicleType type, String make, String model, Integer year,
                          FuelType fuelType, String color, String registrationNumber) {
        var entity = new VehicleEntity(
                null, vin.value(), type.name(), make, model, year,
                fuelType.name(), color, registrationNumber
        );
        var saved = jpa.save(entity);
        return VehicleMapper.toDomain(saved);
    }

    @Transactional
    @Override
    public Vehicle update(VehicleId id, Vin vin, VehicleType type, String make, String model, Integer year,
                          FuelType fuelType, String color, String registrationNumber) {
        var e = jpa.findById(id.value()).orElseThrow(); // service ensures 404 semantics already
        e.setVin(vin.value());
        e.setType(type.name());
        e.setMake(make);
        e.setModel(model);
        e.setModelYear(year);
        e.setFuelType(fuelType.name());
        e.setColor(color);
        e.setRegistrationNumber(registrationNumber);
        return VehicleMapper.toDomain(jpa.save(e));
    }

    @Transactional
    @Override
    public void delete(VehicleId id) {
        jpa.deleteById(id.value());
    }

    // --- spec helpers (unchanged) ---
    static Specification<VehicleEntity> likeIgnoreCaseIfPresent(String field, String value) {
        if (value == null || value.isBlank()) return null;
        final String needle = value.toLowerCase(Locale.ROOT);
        return (root, query, cb) -> cb.equal(cb.lower(root.get(field)), needle);
    }

    static <T> Specification<VehicleEntity> equalsIfPresent(String field, T value) {
        if (value == null) return null;
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    @SafeVarargs
    static Specification<VehicleEntity> andAll(Specification<VehicleEntity>... specs) {
        List<Specification<VehicleEntity>> parts = new ArrayList<>();
        for (Specification<VehicleEntity> s : specs) if (s != null) parts.add(s);
        if (parts.isEmpty()) return Specification.where(null);
        Specification<VehicleEntity> result = parts.get(0);
        for (int i = 1; i < parts.size(); i++) result = result.and(parts.get(i));
        return result;
    }

    private static Specification<VehicleEntity> equalsIgnoreCaseIfPresent(String field, String value) {
        return (root, cq, cb) -> {
            if (value == null || value.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get(field)), value.toLowerCase());
        };
    }
}