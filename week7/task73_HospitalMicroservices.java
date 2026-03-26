// ============================================================
// Task 7.3 - Hospital Management System
// Decentralized Data Management in Microservices
//
// Each service owns its OWN database — no shared DB.
// Services communicate via REST APIs, not direct DB joins.
//
// Service → Database mapping:
//   PatientService   → MySQL (patient_db)
//   DoctorService    → PostgreSQL (doctor_db)
//   AppointmentService → MongoDB (appointment_db)
//
// Run each service independently on different ports:
//   PatientService     → port 8081
//   DoctorService      → port 8082
//   AppointmentService → port 8083
// ============================================================


// ══════════════════════════════════════════════════════════════
// SERVICE 1: Patient Service  (Database: MySQL - patient_db)
// Responsibility: Register and manage patient records
// ══════════════════════════════════════════════════════════════

package com.example.hospital.patient;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Entity stored in MySQL patient_db
@Entity
@Table(name = "patients")
class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int age;
    private String bloodGroup;
    private String contact;

    public Patient() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}

interface PatientRepository extends JpaRepository<Patient, Integer> {}

@RestController
@RequestMapping("/api/patients")
class PatientController {

    private final PatientRepository repo;

    PatientController(PatientRepository repo) { this.repo = repo; }

    @PostMapping
    public Patient register(@RequestBody Patient patient) {
        return repo.save(patient);
    }

    @GetMapping
    public List<Patient> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getById(@PathVariable int id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        repo.deleteById(id);
        return "Patient deleted";
    }
}


// ══════════════════════════════════════════════════════════════
// SERVICE 2: Doctor Service  (Database: PostgreSQL - doctor_db)
// Responsibility: Manage doctor profiles and specializations
// ══════════════════════════════════════════════════════════════

package com.example.hospital.doctor;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Entity
@Table(name = "doctors")
class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String specialization;
    private String department;
    private String contact;

    public Doctor() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String s) { this.specialization = s; }
    public String getDepartment() { return department; }
    public void setDepartment(String d) { this.department = d; }
    public String getContact() { return contact; }
    public void setContact(String c) { this.contact = c; }
}

interface DoctorRepository extends JpaRepository<Doctor, Integer> {}

@RestController
@RequestMapping("/api/doctors")
class DoctorController {

    private final DoctorRepository repo;

    DoctorController(DoctorRepository repo) { this.repo = repo; }

    @PostMapping
    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return repo.save(doctor);
    }

    @GetMapping
    public List<Doctor> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable int id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Doctor update(@PathVariable int id, @RequestBody Doctor updated) {
        updated.setName(updated.getName());
        return repo.save(updated);
    }
}


// ══════════════════════════════════════════════════════════════
// SERVICE 3: Appointment Service  (Database: MongoDB - appointment_db)
// Responsibility: Schedule and track appointments between patients and doctors
// NOTE: Uses patientId and doctorId (no cross-DB joins)
// ══════════════════════════════════════════════════════════════

package com.example.hospital.appointment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "appointments")
class Appointment {

    @Id
    private String id;
    private int patientId;   // Reference to Patient in patient_db
    private int doctorId;    // Reference to Doctor in doctor_db
    private LocalDateTime appointmentTime;
    private String reason;
    private String status;   // SCHEDULED, COMPLETED, CANCELLED

    public Appointment() {}

    public String getId() { return id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int p) { this.patientId = p; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int d) { this.doctorId = d; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime t) { this.appointmentTime = t; }
    public String getReason() { return reason; }
    public void setReason(String r) { this.reason = r; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}

interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByPatientId(int patientId);
    List<Appointment> findByDoctorId(int doctorId);
}

@RestController
@RequestMapping("/api/appointments")
class AppointmentController {

    private final AppointmentRepository repo;

    AppointmentController(AppointmentRepository repo) { this.repo = repo; }

    // Schedule appointment
    // POST /api/appointments
    // Body: { "patientId": 1, "doctorId": 2, "appointmentTime": "2025-04-01T10:00:00", "reason": "Fever" }
    @PostMapping
    public Appointment schedule(@RequestBody Appointment appointment) {
        appointment.setStatus("SCHEDULED");
        return repo.save(appointment);
    }

    @GetMapping
    public List<Appointment> getAll() { return repo.findAll(); }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getByPatient(@PathVariable int patientId) {
        return repo.findByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getByDoctor(@PathVariable int doctorId) {
        return repo.findByDoctorId(doctorId);
    }

    @PutMapping("/{id}/cancel")
    public String cancel(@PathVariable String id) {
        repo.findById(id).ifPresent(a -> {
            a.setStatus("CANCELLED");
            repo.save(a);
        });
        return "Appointment cancelled";
    }

    @PutMapping("/{id}/complete")
    public String complete(@PathVariable String id) {
        repo.findById(id).ifPresent(a -> {
            a.setStatus("COMPLETED");
            repo.save(a);
        });
        return "Appointment completed";
    }
}
