package com.marketing.web.controllers;

import com.marketing.web.dtos.user.ReadableCity;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.UnitType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.user.CityService;
import com.marketing.web.services.user.RoleService;
import com.marketing.web.utils.mappers.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/definitions")
public class DefinitionsController {

    @Autowired
    CityService cityService;

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
    public ResponseEntity<List<ReadableCity>> getCities(){
        return ResponseEntity.ok(cityService.findAll().stream()
                .map(CityMapper.INSTANCE::cityToReadableCity).collect(Collectors.toList()));
    }

    @GetMapping("/cities/{id}/states")
    public ResponseEntity<List<State>> getStatesByCity(@PathVariable String id){
        City city = cityService.findByUuid(id);
        return ResponseEntity.ok(stateRepository.findAllByCity(city));
    }

}
