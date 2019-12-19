package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.models.Credit;
import com.marketing.web.models.User;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.CreditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/credits")
public class CreditsController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableCredit>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalDebt") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        return ResponseEntity.ok(CreditMapper
                .pagedOrderListToWrapperReadableOrder(creditService.findAll(pageNumber, sortBy, sortType)));
    }

    @GetMapping("/my")
    public ResponseEntity<ReadableCredit> getUserCredit(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.findByUser(user.getId())));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ReadableCredit> createCredit(@Valid @RequestBody WritableCredit writableCredit){
        Credit credit = CreditMapper.writableCreditToCredit(writableCredit);
        credit.setUser(userService.findByUUID(writableCredit.getUserId()));
        return new ResponseEntity<>(CreditMapper.creditToReadableCredit(creditService.create(credit)), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> updateCredit(@PathVariable String creditId, @Valid @RequestBody WritableCredit writableCredit){
        Credit credit = CreditMapper.writableCreditToCredit(writableCredit);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.update(creditId, credit)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> deleteCategory(@PathVariable String creditId){
        Credit credit = creditService.findByUUID(creditId);
        creditService.delete(credit);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(credit));
    }
}
