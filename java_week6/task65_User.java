package com.example.auth.model;

import jakarta.validation.constraints.*;

public class User {

    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Must be a valid email address")
    private String email;

    @Min(value = 18, message = "Age must be at least 18")
    private int age;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
