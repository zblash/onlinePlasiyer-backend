package com.marketing.web.controllers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.models.User;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.ObligationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/obligations")
public class ObligationsController {

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableObligation>> getAll(@RequestParam(required = false) Integer pageNumber){
        User user = userService.getLoggedInUser();
        if (pageNumber == null){
            pageNumber=1;
        }
        return ResponseEntity.ok(ObligationMapper.pagedObligationListToWrapperReadableObligation(obligationService.findAllByUser(user,pageNumber)));

    }

    @GetMapping("/totals")
    public ResponseEntity<ReadableTotalObligation> getTotals(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(obligationService.getTotalObligationByUser(user));
    }

    @GetMapping("/totals/byUser/{userId}")
    ResponseEntity<ReadableTotalObligation> getObligationsTotals(@PathVariable String userId){
        User user = userService.findByUUID(userId);
        return ResponseEntity.ok(obligationService.getTotalObligationByUser(user));
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<WrapperPagination<ReadableObligation>> getAllObligations(@PathVariable String userId, @RequestParam(required = false) Integer pageNumber){
        User user = userService.findByUUID(userId);
        if (pageNumber == null){
            pageNumber=1;
        }
        return ResponseEntity.ok(ObligationMapper.pagedObligationListToWrapperReadableObligation(obligationService.findAllByUser(user,pageNumber)));
    }

}
