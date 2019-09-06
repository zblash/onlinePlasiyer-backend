package com.marketing.web.controllers;

import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.UnitType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/definitions")
public class DefinitionsController {

    @Autowired
    CityRepository cityRepository;

    @Autowired
    StateRepository stateRepository;

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(){
        return ResponseEntity.ok(RoleType.values());
    }

    @GetMapping("/unitTypes")
    public  ResponseEntity<?> getUnitTypes(){
        return ResponseEntity.ok(UnitType.values());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<City>> getCities(){
        return ResponseEntity.ok(cityRepository.findAll());
    }

    @GetMapping("/cities/{id}/states")
    public ResponseEntity<List<State>> getStatesByCity(@PathVariable Long id){
        City city = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found with id: "+id));
        return ResponseEntity.ok(stateRepository.findAllByCity(city));
    }

}
