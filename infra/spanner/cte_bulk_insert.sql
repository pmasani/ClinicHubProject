-- The overall effect of this SQL is to automatically generate an event for every patient who recently became 'SCHEDULED'.
-- This event, of type 'BULK_NOTIFICATION', stores basic patient information (first_name, last_name) in a JSON payload,
-- likely to be consumed asynchronously by other services (e.g., a notification service that sends an email or text).
--
-- INSERT INTO patient_events (event_id, patient_id, type, payload, created_at)
--
-- WITH recent AS (
--     SELECT p.patient_id, p.first_name, p.last_name
--     FROM patients p
--     WHERE p.status = 'SCHEDULED'
--       AND p.updated_at > TIMESTAMP_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 HOUR)
-- )
-- SELECT
--     GENERATE_UUID() AS event_id,
--     r.patient_id,
--     'BULK_NOTIFICATION' AS type,
--     TO_JSON_STRING(STRUCT(r.first_name, r.last_name)) AS payload,
--     PENDING_COMMIT_TIMESTAMP()
-- FROM recent r;
INSERT INTO patient_events (event_id, patient_id, type, payload, created_at)
WITH recent AS (
    SELECT p.patient_id, p.first_name, p.last_name
    FROM patients p
    WHERE p.status = 'SCHEDULED'
    -- Temporarily comment this while testing if it inserts 0 rows
    -- AND p.updated_at > TIMESTAMP_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 HOUR)
)
SELECT
    GENERATE_UUID() AS event_id,
    r.patient_id,
    'BULK_NOTIFICATION' AS type,
    -- emulator-safe JSON-ish string
    CONCAT(
            '{"firstName":"', r.first_name,
            '","lastName":"', r.last_name, '"}'
    ) AS payload,
    CURRENT_TIMESTAMP()  -- <-- use this instead of PENDING_COMMIT_TIMESTAMP()
FROM recent r;
