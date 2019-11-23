package com.marketing.web.controllers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.ObligationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/totals")
    public ResponseEntity<ReadableTotalObligation> getTotals(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(obligationService.getTotalObligationByUser(user));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<WrapperPagination<ReadableObligation>> getAll(String id,@RequestParam(required = false) Integer pageNumber){
        User user = userService.findByUUID(id);
        if (pageNumber == null){
            pageNumber=1;
        }
        return ResponseEntity.ok(ObligationMapper.pagedObligationListToWrapperReadableObligation(obligationService.findAllByUser(user,pageNumber)));
    }

}
