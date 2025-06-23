package com.fintrack.fintrack_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
public class DbTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test-db")
    public ResponseEntity<String> testDb() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {

            if (rs.next()) {
                return ResponseEntity.ok("DB connection successful! Result: " + rs.getInt(1));
            } else {
                return ResponseEntity.ok("DB connection successful but no results");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("DB connection failed: " + e.getMessage());
        }
    }
}
