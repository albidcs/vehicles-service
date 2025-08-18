
ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS vin VARCHAR(17);
ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS type VARCHAR(20);
ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS fuel_type VARCHAR(20);
ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS color VARCHAR(40);
ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS registration_number VARCHAR(20);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'uk_vehicles_vin'
    ) THEN
ALTER TABLE vehicles ADD CONSTRAINT uk_vehicles_vin UNIQUE (vin);
END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'uk_vehicles_registration'
    ) THEN
ALTER TABLE vehicles ADD CONSTRAINT uk_vehicles_registration UNIQUE (registration_number);
END IF;
END$$;