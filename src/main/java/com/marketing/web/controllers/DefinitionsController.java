package com.marketing.web.controllers;

import com.marketing.web.dtos.order.OrderItemPDF;
import com.marketing.web.dtos.user.readable.ReadableCity;
import com.marketing.web.dtos.user.readable.ReadableState;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.UnitType;
import com.marketing.web.models.City;
import com.marketing.web.models.Order;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.user.CityService;
import com.marketing.web.services.user.StateService;
import com.marketing.web.utils.PdfGenerator;
import com.marketing.web.utils.mappers.CityMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class DefinitionsController {

    private final CityService cityService;

    private final StateService stateService;

    private final PdfGenerator pdfGenerator;

    public DefinitionsController(CityService cityService, StateService stateService, PdfGenerator pdfGenerator) {
        this.cityService = cityService;
        this.stateService = stateService;
        this.pdfGenerator = pdfGenerator;
    }

    @GetMapping("/definitions/roles")
    public ResponseEntity<?> getRoles(){
        return ResponseEntity.ok(RoleType.values());
    }

    @GetMapping("/definitions/unitTypes")
    public  ResponseEntity<?> getUnitTypes(){
        return ResponseEntity.ok(UnitType.values());
    }

    @GetMapping("/definitions/cities")
    public ResponseEntity<List<ReadableCity>> getCities(){
        return ResponseEntity.ok(cityService.findAll().stream()
                .map(CityMapper::cityToReadableCity).collect(Collectors.toList()));
    }

    @GetMapping("/definitions/cities/{id}/states")
    public ResponseEntity<List<ReadableState>> getStatesByCity(@PathVariable String id){
        City city = cityService.findById(id);
        return ResponseEntity.ok(stateService.findAllByCity(city).stream()
                .map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/definitions/states")
    public ResponseEntity<List<ReadableState>> getStates(){
        return ResponseEntity.ok(stateService.findAll().stream()
                .map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/health")
    public ResponseEntity<?> getMap(){
        return ResponseEntity.ok("OK");
    }

}
