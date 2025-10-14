package com.clinichub.patient_service.model;

import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.spanner.core.mapping.*;


@Table(name = "patient_events")
public class PatientEvent {
    @PrimaryKey
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "type")
    private String type;

    @Column(name = "payload")
    private String payload; // raw JSON

//    @CommitTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    public PatientEvent() {}
    // getters/setters...
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
