package io.github.albi.vehicles.adapters.persistence.vehicle;


import io.github.albi.vehicles.domain.vehicle.Vehicle;
import io.github.albi.vehicles.domain.vehicle.VehicleId;
import io.github.albi.vehicles.domain.vehicle.VehicleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Persistence adapter: implements the domain VehicleRepository
 * using Spring Data JPA under the hood. Keeps the domain decoupled from JPA.
 */
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
    public List<Vehicle> search(String make, String model, Integer year) {
        // Build a dynamic Specification only for provided filters (case-insensitive for make/model).
        Specification<VehicleEntity> spec = andAll(
                likeIgnoreCaseIfPresent("make", make),
                likeIgnoreCaseIfPresent("model", model),
                equalsIfPresent("year", year)
        );

        return jpa.findAll(spec).stream()
                .map(VehicleMapper::toDomain)
                .toList();
    }

    // ---- Specification helpers (package-private) ----

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
        for (Specification<VehicleEntity> s : specs) {
            if (s != null) parts.add(s);
        }
        if (parts.isEmpty()) return Specification.where(null);
        Specification<VehicleEntity> result = parts.get(0);
        for (int i = 1; i < parts.size(); i++) result = result.and(parts.get(i));
        return result;
    }
}