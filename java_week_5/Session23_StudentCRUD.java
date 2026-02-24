package com.week5.session23;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class Session23_StudentCRUD implements CommandLineRunner {

    @Autowired
    private StudentCrudService service;

    public static void main(String[] args) {
        SpringApplication.run(Session23_StudentCRUD.class, args);
    }

    @Override
    public void run(String... args) {
        service.create(new StudentEntity("Ravi Kumar", "CSE", 20, "ravi@mail.com"));
        service.create(new StudentEntity("Priya Sharma", "ECE", 21, "priya@mail.com"));
        service.create(new StudentEntity("Arjun Singh", "CSE", 22, "arjun@mail.com"));
        System.out.println("Session 23 — Student CRUD ready. Test via Postman.");
        System.out.println("   Base URL: http://localhost:8085/students");
    }
}

@Entity
@Table(name = "students")
class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column
    private String department;
    @Column
    private int age;
    @Column(unique = true)
    private String email;

    public StudentEntity() {
    }

    public StudentEntity(String name, String department, int age, String email) {
        this.name = name;
        this.department = department;
        this.age = age;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        this.name = v;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String v) {
        this.department = v;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int v) {
        this.age = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }
}

interface StudentCrudRepository extends JpaRepository<StudentEntity, Long> {
}

@Service
class StudentCrudService {

    @Autowired
    private StudentCrudRepository repo;

    public StudentEntity create(StudentEntity s) {
        return repo.save(s);
    }

    public List<StudentEntity> getAll() {
        return repo.findAll();
    }

    public Optional<StudentEntity> getById(Long id) {
        return repo.findById(id);
    }

    public StudentEntity update(Long id, StudentEntity updated) {
        return repo.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDepartment(updated.getDepartment());
            existing.setAge(updated.getAge());
            existing.setEmail(updated.getEmail());
            return repo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Student not found: " + id));
    }

    public String delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Deleted student ID " + id;
        }
        return "Student ID " + id + " not found.";
    }
}

@RestController
@RequestMapping("/students")
class StudentCrudController {

    @Autowired
    private StudentCrudService service;

    @PostMapping
    public ResponseEntity<StudentEntity> create(@RequestBody StudentEntity student) {
        return ResponseEntity.ok(service.create(student));
    }

    @GetMapping
    public ResponseEntity<List<StudentEntity>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentEntity> update(@PathVariable Long id,
            @RequestBody StudentEntity student) {
        return ResponseEntity.ok(service.update(id, student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
