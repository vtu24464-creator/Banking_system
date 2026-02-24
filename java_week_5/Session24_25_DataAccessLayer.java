package com.week5.session24_25;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class Session24_25_DataAccessLayer implements CommandLineRunner {

    @Autowired
    private StudentDataService service;

    public static void main(String[] args) {
        SpringApplication.run(Session24_25_DataAccessLayer.class, args);
    }

    @Override
    public void run(String... args) {
        service.save(new StudentData("Ravi Kumar", "CSE", 20, "ravi@mail.com"));
        service.save(new StudentData("Priya Sharma", "ECE", 22, "priya@mail.com"));
        service.save(new StudentData("Arjun Singh", "CSE", 21, "arjun@mail.com"));
        service.save(new StudentData("Meena Patel", "IT", 23, "meena@mail.com"));
        service.save(new StudentData("Kiran Das", "CSE", 20, "kiran@mail.com"));
        service.save(new StudentData("Sunita Roy", "ECE", 24, "sunita@mail.com"));
        System.out.println("Session 24/25 — Data Access Layer ready.");
        System.out.println("   Test: GET http://localhost:8085/data/students");
    }
}

@Entity
@Table(name = "students")
class StudentData {

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

    public StudentData() {
    }

    public StudentData(String name, String department, int age, String email) {
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
        name = v;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String v) {
        department = v;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int v) {
        age = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }
}

interface StudentDataRepository extends JpaRepository<StudentData, Long> {

    List<StudentData> findByDepartment(String department);

    List<StudentData> findByAge(int age);

    List<StudentData> findByAgeGreaterThan(int age);

    List<StudentData> findByAgeLessThan(int age);

    List<StudentData> findByAgeBetween(int minAge, int maxAge);

    List<StudentData> findByDepartmentAndAge(String department, int age);

    List<StudentData> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT s FROM StudentData s WHERE s.department = :dept ORDER BY s.name")
    List<StudentData> findByDeptOrdered(@Param("dept") String dept);

    @Query("SELECT s FROM StudentData s WHERE s.department = :dept AND s.age > :age")
    List<StudentData> findByDeptAndAgeGreaterThan(@Param("dept") String dept,
            @Param("age") int age);

    @Query(value = "SELECT department, COUNT(*) as total FROM students GROUP BY department", nativeQuery = true)
    List<Object[]> countByDepartment();

    @Query("SELECT s FROM StudentData s WHERE s.department = :dept ORDER BY s.age ASC")
    List<StudentData> findYoungestInDept(@Param("dept") String dept);
}

@Service
class StudentDataService {

    @Autowired
    private StudentDataRepository repo;

    public StudentData save(StudentData s) {
        return repo.save(s);
    }

    public List<StudentData> getAll() {
        return repo.findAll();
    }

    public Optional<StudentData> getById(Long id) {
        return repo.findById(id);
    }

    public List<StudentData> getByDept(String dept) {
        return repo.findByDepartment(dept);
    }

    public List<StudentData> getByAge(int age) {
        return repo.findByAge(age);
    }

    public List<StudentData> getByAgeGreaterThan(int age) {
        return repo.findByAgeGreaterThan(age);
    }

    public List<StudentData> getByAgeBetween(int min, int max) {
        return repo.findByAgeBetween(min, max);
    }

    public List<StudentData> getByDeptAndAge(String dept, int age) {
        return repo.findByDepartmentAndAge(dept, age);
    }

    public List<StudentData> searchByName(String kw) {
        return repo.findByNameContainingIgnoreCase(kw);
    }

    public List<StudentData> getByDeptOrdered(String dept) {
        return repo.findByDeptOrdered(dept);
    }

    public List<StudentData> getByDeptAgeGT(String dept, int age) {
        return repo.findByDeptAndAgeGreaterThan(dept, age);
    }

    public List<Object[]> getDeptCount() {
        return repo.countByDepartment();
    }

    public List<StudentData> getYoungestInDept(String dept) {
        return repo.findYoungestInDept(dept);
    }

    public long totalCount() {
        return repo.count();
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}

@RestController
@RequestMapping("/data/students")
class StudentDataController {

    @Autowired
    private StudentDataService service;

    @GetMapping
    public ResponseEntity<List<StudentData>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dept/{dept}")
    public ResponseEntity<List<StudentData>> getByDept(@PathVariable String dept) {
        return ResponseEntity.ok(service.getByDept(dept));
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<List<StudentData>> getByAge(@PathVariable int age) {
        return ResponseEntity.ok(service.getByAge(age));
    }

    @GetMapping("/age/above/{age}")
    public ResponseEntity<List<StudentData>> getAgeAbove(@PathVariable int age) {
        return ResponseEntity.ok(service.getByAgeGreaterThan(age));
    }

    @GetMapping("/age/range/{min}/{max}")
    public ResponseEntity<List<StudentData>> getAgeBetween(@PathVariable int min,
            @PathVariable int max) {
        return ResponseEntity.ok(service.getByAgeBetween(min, max));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<StudentData>> getByDeptAndAge(@RequestParam String dept,
            @RequestParam int age) {
        return ResponseEntity.ok(service.getByDeptAndAge(dept, age));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentData>> search(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/dept/{dept}/sorted")
    public ResponseEntity<List<StudentData>> getSortedByDept(@PathVariable String dept) {
        return ResponseEntity.ok(service.getByDeptOrdered(dept));
    }

    @GetMapping("/dept/{dept}/age/above/{age}")
    public ResponseEntity<List<StudentData>> getDeptAgeGT(@PathVariable String dept,
            @PathVariable int age) {
        return ResponseEntity.ok(service.getByDeptAgeGT(dept, age));
    }

    @GetMapping("/count/dept")
    public ResponseEntity<List<Object[]>> countByDept() {
        return ResponseEntity.ok(service.getDeptCount());
    }

    @GetMapping("/dept/{dept}/youngest")
    public ResponseEntity<List<StudentData>> youngest(@PathVariable String dept) {
        return ResponseEntity.ok(service.getYoungestInDept(dept));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.totalCount());
    }

    @PostMapping
    public ResponseEntity<StudentData> create(@RequestBody StudentData s) {
        return ResponseEntity.ok(service.save(s));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok("Deleted student ID: " + id);
    }
}
