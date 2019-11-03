package com.marketing.web.controllers;

import com.marketing.web.dtos.obligation.WrapperReadableObligation;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@RestController
@RequestMapping("/api/obligations")
public class ObligationsController {

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping
    public ResponseEntity<WrapperReadableObligation> getAll(){
        User user = userService.getLoggedInUser();

        List<Obligation> obligations = obligationService.findAllByUser(user);
        WrapperReadableObligation wrapperReadableObligation = new WrapperReadableObligation();
        wrapperReadableObligation.setValues(obligations.stream().map(ObligationMapper::obligationToReadableObligation).collect(Collectors.toList()));
        wrapperReadableObligation.setTotalDebts(obligations.stream().flatMapToDouble(obligation -> DoubleStream.of(obligation.getDebt())).sum());
        wrapperReadableObligation.setTotalReceivables(obligations.stream().flatMapToDouble(obligation -> DoubleStream.of(obligation.getReceivable())).sum());
        return ResponseEntity.ok(wrapperReadableObligation);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<WrapperReadableObligation> getAll(String id){
        User user = userService.findByUUID(id);

        List<Obligation> obligations = obligationService.findAllByUser(user);
        WrapperReadableObligation wrapperReadableObligation = new WrapperReadableObligation();
        wrapperReadableObligation.setValues(obligations.stream().map(ObligationMapper::obligationToReadableObligation).collect(Collectors.toList()));
        wrapperReadableObligation.setTotalDebts(obligations.stream().flatMapToDouble(obligation -> DoubleStream.of(obligation.getDebt())).sum());
        wrapperReadableObligation.setTotalReceivables(obligations.stream().flatMapToDouble(obligation -> DoubleStream.of(obligation.getReceivable())).sum());
        return ResponseEntity.ok(wrapperReadableObligation);
    }

}
