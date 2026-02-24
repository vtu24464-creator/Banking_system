package com.week5.session21;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Session21_JdbcConfig implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Session21_JdbcConfig.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  Session 21 — JDBC Connection Test");
        System.out.println("=".repeat(50));
        try {
            String dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            System.out.println("  JDBC Connected! DB Version: " + dbVersion);
        } catch (Exception e) {
            System.out.println("  DB not reachable — using H2 in-memory fallback.");
            System.out.println("     Error: " + e.getMessage());
        }
        System.out.println("  Test: GET http://localhost:8085/db/test");
        System.out.println("=".repeat(50) + "\n");
    }
}

@RestController
@RequestMapping("/db")
class JdbcTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public String testConnection() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "JDBC Connection SUCCESS — SELECT 1 returned: " + result;
        } catch (Exception e) {
            return "JDBC Connection FAILED: " + e.getMessage();
        }
    }

    @GetMapping("/version")
    public String dbVersion() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            return "Database Version: " + version;
        } catch (Exception e) {
            return "Could not fetch version: " + e.getMessage();
        }
    }
}
