package com.marketing.web.controllers;

import com.marketing.web.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class HealthController {

    @Autowired
    private Runner runner;

    @GetMapping("/health")
    public ResponseEntity<?> getMap(){
        return ResponseEntity.ok("OK");
    }

}
