package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.dtos.obligation.ReadableObligationActivity;
import com.marketing.web.dtos.obligation.WritableObligation;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.SearchOperations;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.ObligationActivity;
import com.marketing.web.services.invoice.ObligationActivityService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.user.RoleService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.specifications.SearchSpecificationBuilder;
import com.marketing.web.utils.mappers.ObligationMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/private/obligations")
public class ObligationsController {

    private final ObligationService obligationService;

    private final ObligationActivityService obligationActivityService;

    private final MerchantService merchantService;

    private final UserService userService;

    private final RoleService roleService;

    public ObligationsController(ObligationService obligationService, ObligationActivityService obligationActivityService, MerchantService merchantService, UserService userService, RoleService roleService) {
        this.obligationService = obligationService;
        this.obligationActivityService = obligationActivityService;
        this.merchantService = merchantService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableObligation>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        return ResponseEntity.ok(ObligationMapper.pagedObligationListToWrapperReadableObligation(obligationService.findAll(pageNumber, sortBy, sortType)));

    }

    @GetMapping("/totals")
    public ResponseEntity<ReadableObligation> getTotals(){
        return ResponseEntity.ok(ObligationMapper.obligationToReadableObligation(obligationService.findByMerchant(merchantService.getLoggedInMerchant())));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/totals/byUser/{userId}")
    public ResponseEntity<ReadableObligation> getObligationByUser(@PathVariable String userId){
        return ResponseEntity.ok(ObligationMapper.obligationToReadableObligation(obligationService.findByMerchant(merchantService.findById(userId))));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableObligation> updateObligation(@PathVariable String id, @RequestBody WritableObligation writableObligation) {
        if (writableObligation.getDebt() == null && writableObligation.getReceivable() == null) {
            throw new BadRequestException("Must not null atleast one field");
        }
        Obligation obligation = obligationService.findById(id);
        if (writableObligation.getReceivable() != null) {
            obligation.setReceivable(writableObligation.getReceivable());
        }
        if (writableObligation.getDebt() != null) {
            obligation.setDebt(writableObligation.getDebt());
        }
        return ResponseEntity.ok(ObligationMapper.obligationToReadableObligation(obligationService.update(id, obligation)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @GetMapping("/activities")
    public ResponseEntity<WrapperPagination<ReadableObligationActivity>> getActivities(@RequestParam(required = false) String merchantId, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate lastDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        RoleType roleType = roleService.getLoggedInUserRole();
        SearchSpecificationBuilder<ObligationActivity> searchBuilder = new SearchSpecificationBuilder<>();

        if (startDate != null) {
            searchBuilder.add("date", SearchOperations.GREATER_THAN, startDate, false);
            if (lastDate != null) {
                searchBuilder.add("date", SearchOperations.LESS_THAN, lastDate, false);
            }
        }

        if (roleType.equals(RoleType.ADMIN) && (merchantId != null && !merchantId.isEmpty())) {
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchantService.findById(merchantId), false);
        } else {
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchantService.getLoggedInMerchant(), false);
        }

        return ResponseEntity.ok(ObligationMapper.pagedObligationActivityListToWrapperReadableObligationActivity(
                obligationActivityService.findAllByMerchant(merchantService.getLoggedInMerchant(), pageNumber, sortBy, sortType)
        ));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/activities/byUser/{userId}")
    public ResponseEntity<WrapperPagination<ReadableObligationActivity>> getAllObligations(@PathVariable String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        return ResponseEntity.ok(ObligationMapper.pagedObligationActivityListToWrapperReadableObligationActivity(
                obligationActivityService.findAllByMerchant(merchantService.findById(userId), pageNumber, sortBy, sortType)
        ));
    }

}
