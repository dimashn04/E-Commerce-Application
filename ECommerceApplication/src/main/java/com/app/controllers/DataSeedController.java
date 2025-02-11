package com.app.controllers;

import com.app.services.DataSeedService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataSeedController {

    private final DataSeedService dataSeedService;

    public DataSeedController(DataSeedService dataSeedService) {
        this.dataSeedService = dataSeedService;
    }

    @PermitAll
    @GetMapping("/seed-all")
    public ResponseEntity<String> seedAll() {
        System.out.println("Seeding started...");
        dataSeedService.seedData();
        System.out.println("Seeding completed.");
        return ResponseEntity.ok("Seeding all data completed successfully.");
    }

}