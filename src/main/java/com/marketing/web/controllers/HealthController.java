package com.marketing.web.controllers;

import com.marketing.web.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

@RestController
public class HealthController {

    @Autowired
    private Runner runner;

    @GetMapping("/health")
    public ResponseEntity<?> getMap( HttpServletRequest request){
        return ResponseEntity.ok(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath());
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refreshDb() throws URISyntaxException {
        runner.dropTables();
        runner.populator();
        return ResponseEntity.ok("Db refreshed");
    }
}
