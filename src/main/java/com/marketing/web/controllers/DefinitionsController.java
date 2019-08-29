package com.marketing.web.controllers;

import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.UnitType;
import com.marketing.web.services.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/definitions")
public class DefinitionsController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(){
        return ResponseEntity.ok(RoleType.values());
    }

    @GetMapping("/unitTypes")
    public  ResponseEntity<?> getUnitTypes(){
        return ResponseEntity.ok(UnitType.values());
    }

}
