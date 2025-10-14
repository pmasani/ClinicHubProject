CREATE TABLE patients (
  patient_id STRING(36) NOT NULL,
  first_name STRING(100),
  last_name STRING(100),
  dob DATE,
  phone STRING(30),
  status STRING(20),
  updated_at TIMESTAMP OPTIONS (allow_commit_timestamp=true)
) PRIMARY KEY (patient_id);

CREATE TABLE patient_events (
  event_id STRING(36) NOT NULL,
  patient_id STRING(36) NOT NULL,
  type STRING(30),
  payload STRING(MAX),
  created_at TIMESTAMP OPTIONS (allow_commit_timestamp=true)
) PRIMARY KEY (event_id);
