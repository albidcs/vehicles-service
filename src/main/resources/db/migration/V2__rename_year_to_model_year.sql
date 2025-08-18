-- Rename column year -> model_year if it exists (PostgreSQL-safe)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'vehicles'
          AND column_name = 'year'
    ) THEN
ALTER TABLE vehicles RENAME COLUMN year TO model_year;
END IF;
END $$;

-- Recreate index for the renamed column
DROP INDEX IF EXISTS idx_vehicles_year;
CREATE INDEX IF NOT EXISTS idx_vehicles_model_year ON vehicles(model_year);