package com.week5.session22;

import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Session22_JpaEntity {
    public static void main(String[] args) {
        SpringApplication.run(Session22_JpaEntity.class, args);
        System.out.println("Session 22 — JPA Entity mapping started.");
        System.out.println("   Hibernate will auto-create 'students' table from @Entity.");
    }
}

@Entity
@Table(name = "students")
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Column(name = "student_name", nullable = false, length = 100)
    private String name;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "age")
    private int age;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    public Student() {
    }

    public Student(String name, String department, int age, String email) {
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

    public void setName(String n) {
        this.name = n;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String d) {
        this.department = d;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int a) {
        this.age = a;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', dept='" +
                department + "', age=" + age + ", email='" + email + "'}";
    }
}

@RestController
@RequestMapping("/student")
class StudentSchemaController {

    @GetMapping("/schema")
    public String schema() {
        return "ORM Mapping Active:\n" +
                "Class     : Student\n" +
                "Table     : students\n" +
                "PK Column : student_id  (@Id + @GeneratedValue)\n" +
                "Columns   : student_name, department, age, email\n" +
                "Hibernate : will auto-create table on startup (ddl-auto=update)";
    }
}
