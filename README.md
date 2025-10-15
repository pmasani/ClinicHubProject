
# ClinicHub 

**ClinicHubProject** is a small example of a healthcare app. Think of it like two small helpers that talk to each other:

- **Patient Service** — keeps a simple list of patients.
- **Visit Service** — keeps a simple list of doctor visits.

- When **Visit Service** needs patient info, it calls **Patient Service** via **OpenFeign** (a safe, built-in HTTP client).
- Data is stored locally using the **Google Cloud Spanner Emulator**, so you don’t need a real cloud account to try it.
- Each change to a visit (created, updated, canceled) emits a **Kafka event** (`visit-events`) so other parts can react.
- *(Optional)* It can also **broadcast updates** using **MQTT**, handy for lightweight devices/dashboards.


---

## 📂 Project Structure

```
clinichub/
├── patient-service/        # Patient CRUD + Spanner persistence
├── visit-service/          # Start visit + Feign to patient-service + publishes Kafka events
└── infra/
    ├── kafka/              # Docker Compose (Kafka, Zookeeper)
    ├── mqtt/               # Docker Compose (Mosquitto)
    └── spanner/
        ├── schema.sql      # Database schema (DDL)
        └── (seed.sql?)     # Optional seed data
```

---

## ⚙️ Prerequisites

* Java **21**
* Maven **3.9+**
* Google Cloud SDK (`gcloud`) with **Spanner Emulator**
* Docker + Docker Compose
* `curl` + `jq`
* Postman (optional)
* (Optional, for MQTT bridge) `kcat`, `mosquitto`, `jq`

---

## 🚀 Getting Started

### 1) Clone

```bash
git clone https://github.com/<your-username>/clinichub.git
cd clinichub
```

### 2) Start Spanner Emulator

**Terminal A (leave running):**

```bash
gcloud emulators spanner start --host-port=0.0.0.0:9010
```

**Terminal B (init env + create DB):**

```bash
# point this shell to the emulator & a local project
$(gcloud emulators spanner env-init)
gcloud config set auth/disable_credentials true
gcloud config set project demo-proj
export SPANNER_EMULATOR_HOST=localhost:9010

# create instance + database + schema
gcloud spanner instances create clinic-instance --config=emulator-config --description="ClinicHub Emulator" --nodes=1
gcloud spanner databases create clinicdb --instance=clinic-instance
gcloud spanner databases ddl update clinicdb --instance=clinic-instance \
  --ddl="$(cat infra/spanner/schema.sql)"
```

---

## ▶️ Running Services

> Keep `SPANNER_EMULATOR_HOST=localhost:9010` in the **environment** of any terminal running patient-service.

### Patient Service (port 8081)

```bash
export SPANNER_EMULATOR_HOST=localhost:9010
cd patient-service
mvn -q spring-boot:run -Dspring-boot.run.profiles=emulator
```

### Visit Service (port 8082)

```bash
cd visit-service
mvn -q spring-boot:run -Dspring-boot.run.profiles=dev
```

**Health checks:**

```bash
curl -s http://localhost:8081/actuator/health   # {"status":"UP"}
curl -s http://localhost:8082/actuator/health   # {"status":"UP"}
```

---

## 🧵 Kafka & MQTT (local)

### Start Kafka

```bash
cd infra/kafka
docker compose up -d
```

Create topics (idempotent) and verify:

```bash
docker exec -it clinic-kafka bash -lc "/usr/bin/kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --topic visit-events --partitions 1 --replication-factor 1"
docker exec -it clinic-kafka bash -lc "/usr/bin/kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --topic patient-events --partitions 1 --replication-factor 1"
docker exec -it clinic-kafka bash -lc "/usr/bin/kafka-topics --bootstrap-server localhost:9092 --list"
```

**Optional live tail during demo** (leave running):

```bash
docker exec -it clinic-kafka bash -lc \
"/usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 \
 --topic visit-events --from-beginning \
 --consumer-property group.id=visit-tail \
 --consumer-property auto.offset.reset=latest"
```

### Start MQTT

```bash
cd infra/mqtt
docker compose up -d
```

**Subscriber** (leave running):

```bash
mosquitto_sub -h localhost -t "clinic/visits/#" -v
```

**(Optional) Quick Kafka→MQTT bridge (on your Mac):**

```bash
# requires: brew install kcat jq mosquitto
kcat -b localhost:9092 -t visit-events -f '%s\n' -q \
| while IFS= read -r line; do
  pid=$(printf '%s' "$line" | jq -r '.patientId // "unknown"')
  mosquitto_pub -h localhost -t "clinic/visits/$pid" -m "$line"
done
```

---

## 🌱 (Optional) Seed Data

### A – via SQL

```bash
gcloud spanner databases execute-sql clinicdb --instance=clinic-instance \
  --sql="$(cat infra/spanner/seed.sql)"
```

### B – via API (example script)

```bash
# create one and capture PID
PID=$(curl -s -X POST http://localhost:8081/patients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ava","lastName":"Lee","dob":"2000-09-09","phone":"555-111","status":"SCHEDULED"}' \
  | jq -r .patientId); echo "PatientID=$PID"
```

---

## 🧪 Flow

### 1) Create Patient

```bash
PID=$(curl -s -X POST http://localhost:8081/patients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ava","lastName":"Lee","dob":"2000-09-09","phone":"555-111","status":"SCHEDULED"}' \
  | jq -r .patientId); echo "PID=$PID"
```

### 2) Get Patient

```bash
curl -s "http://localhost:8081/patients/$PID" | jq
```

### 3) Start Visit  ➜ publishes Kafka `VISIT_STARTED`

```bash
curl -s -X POST "http://localhost:8082/visits/start?patientId=$PID" | jq
```

**See it in Kafka** (offsets & messages):

```bash
docker exec -it clinic-kafka bash -lc "/usr/bin/kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic visit-events"
docker exec -it clinic-kafka bash -lc "/usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic visit-events --partition 0 --offset 0 --max-messages 100 --isolation-level read_uncommitted"
```

**See it in MQTT** (if bridge running):

```
clinic/visits/<PID> {... VISIT_STARTED ...}
```

### 4) Visit Status (if implemented)

```bash
curl -s "http://localhost:8082/visits/$PID/status" | jq
```

### 5) Update Patient

```bash
curl -s -X PUT "http://localhost:8081/patients/$PID" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ava","lastName":"Lee","dob":"2000-09-09","phone":"555-333","status":"SEATED"}' | jq
```

### 6) Delete Patient

```bash
curl -s -X DELETE "http://localhost:8081/patients/$PID" -w "HTTP %{http_code}\n"
```

---

## 🧭 Postman (optional)

Create an environment **ClinicHub (Local)**:

```
baseUrlPatient = http://localhost:8081
baseUrlVisit   = http://localhost:8082
PID            = (leave blank; Create Patient test sets it)
```

Requests:

* **Create Patient** (Tests):

  ```js
  pm.test("201/200", () => [200,201].includes(pm.response.code));
  const j = pm.response.json();
  pm.expect(j).to.have.property("patientId");
  pm.environment.set("PID", j.patientId);
  ```
* **Start Visit**: `POST {{baseUrlVisit}}/visits/start`
  Params: `patientId = {{PID}}` (Body: none)

---

## 📊 What’s Implemented

* ✅ **Patient Service**: CRUD + Spanner persistence (emulator locally)
* ✅ **Visit Service**: Starts visit → emits `VISIT_STARTED` to Kafka; Feign to patient-service
* ✅ **End-to-end flow**: Create → Read → Visit → Update → Delete
* ✅ **Postman collection** (optional)
* ✅ **Kafka + MQTT ** (optional bridge)

---

## 🧩 Troubleshooting (quick)

* **500 on `/visits/start`** → send `patientId` as **query param**, not JSON body:

  ```
  POST /visits/start?patientId=<uuid>
  ```
* **Consumer reads 0** but you produced → read deterministically:

  ```bash
  /usr/bin/kafka-console-consumer --partition 0 --offset 0 --max-messages 50 ...
  ```
* **Offsets didn’t move** → the message didn’t send; use non-interactive producer:

  ```bash
  printf '%s\n' '{"event":"PING","patientId":"test"}' | /usr/bin/kafka-console-producer ...
  ```
* **MQTT blank** → start mosquitto and/or the Kafka→MQTT bridge.

---

## 📌 Roadmap

* More visit events (`VISIT_ENDED`, `VISIT_UPDATED`)
* Native Kafka consumer(s) (analytics, notifications)
* Persist MQTT to time-series / monitoring
* Swagger/OpenAPI docs
* Spring Cloud Config
