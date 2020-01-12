package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.models.SystemCredit;
import com.marketing.web.models.User;
import com.marketing.web.services.credit.SystemCreditService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.CreditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/credits")
public class CreditsController {

    @Autowired
    private SystemCreditService systemCreditService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableCredit>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalDebt") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        return ResponseEntity.ok(CreditMapper
                .pagedOrderListToWrapperReadableOrder(systemCreditService.findAll(pageNumber, sortBy, sortType)));
    }

    @GetMapping("/my")
    public ResponseEntity<ReadableCredit> getUserCredit(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(systemCreditService.findByUser(user.getId())));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<ReadableCredit> getCreditByUser(@PathVariable String userId){
        User user = userService.findByUUID(userId);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(systemCreditService.findByUser(user.getId())));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> updateCredit(@PathVariable String creditId, @Valid @RequestBody WritableCredit writableCredit){
        SystemCredit systemCredit = CreditMapper.writableCreditToCredit(writableCredit);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(systemCreditService.update(creditId, systemCredit)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> deleteCategory(@PathVariable String creditId){
        SystemCredit systemCredit = systemCreditService.findByUUID(creditId);
        systemCreditService.delete(systemCredit);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(systemCredit));
    }
}
