package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.dtos.obligation.ReadableObligationActivity;
import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.dtos.obligation.WritableObligation;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import com.marketing.web.services.invoice.ObligationActivityService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.ObligationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/obligations")
public class ObligationsController {

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private ObligationActivityService obligationActivityService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableObligation>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        return ResponseEntity.ok(ObligationMapper.pagedObligationListToWrapperReadableObligation(obligationService.findAll(pageNumber, sortBy, sortType)));

    }

    @GetMapping("/totals")
    public ResponseEntity<ReadableObligation> getTotals(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(ObligationMapper.obligationToReadableObligation(obligationService.findByUser(user)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/totals/byUser/{userId}")
    public ResponseEntity<ReadableObligation> getObligationByUser(@PathVariable String userId){
        User user = userService.findByUUID(userId);
        return ResponseEntity.ok(ObligationMapper.obligationToReadableObligation(obligationService.findByUser(user)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableObligation> updateObligation(@PathVariable String id, @RequestBody WritableObligation writableObligation) {
        if (writableObligation.getDebt() == null && writableObligation.getReceivable() == null) {
            throw new BadRequestException("Must not null atleast one field");
        }
        Obligation obligation = obligationService.findByUuid(id);
        if (writableObligation.getReceivable() != null) {
            obligation.setReceivable(writableObligation.getReceivable());
        }
        if (writableObligation.getDebt() != null) {
            obligation.setDebt(writableObligation.getDebt());
        }
        return ResponseEntity.ok(ObligationMapper.obligationToReadableObligation(obligationService.update(id, obligation)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/activities")
    public ResponseEntity<WrapperPagination<ReadableObligationActivity>> getActivities(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        Obligation obligation = obligationService.findByUser(userService.getLoggedInUser());
        return ResponseEntity.ok(ObligationMapper.pagedObligationActivityListToWrapperReadableObligationActivity(
                obligationActivityService.findAllByObligation(obligation, pageNumber, sortBy, sortType)
        ));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/activities/byUser/{userId}")
    public ResponseEntity<WrapperPagination<ReadableObligationActivity>> getAllObligations(@PathVariable String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        Obligation obligation = obligationService.findByUuid(userId);
        return ResponseEntity.ok(ObligationMapper.pagedObligationActivityListToWrapperReadableObligationActivity(
                obligationActivityService.findAllByObligation(obligation, pageNumber, sortBy, sortType)
        ));
    }

}
