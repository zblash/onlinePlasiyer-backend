package com.marketing.web.controllers.admin;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableActivity;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.enums.*;
import com.marketing.web.models.Activity;
import com.marketing.web.models.Credit;
import com.marketing.web.models.Customer;
import com.marketing.web.models.User;
import com.marketing.web.services.credit.ActivityService;
import com.marketing.web.services.credit.CreditActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.user.CustomerService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.specifications.SearchSpecificationBuilder;
import com.marketing.web.utils.mappers.CreditMapper;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/private/admin/credits")
public class SystemCreditsController {

    private final CreditService creditService;

    private final UserService userService;

    private final ActivityService activityService;

    private final CustomerService customerService;

    public SystemCreditsController(CreditService creditService, CreditActivityService creditActivityService, UserService userService, ActivityService activityService, CustomerService customerService) {
        this.creditService = creditService;
        this.userService = userService;
        this.activityService = activityService;
        this.customerService = customerService;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, null,  new CustomDateEditor(dateFormat, false));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableCredit>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalDebt") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        return ResponseEntity.ok(CreditMapper
                .pagedCreditListToWrapperReadableCredit(creditService.findAllByCreditType(pageNumber, sortBy, sortType, CreditType.SYSTEM_CREDIT)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<ReadableCredit> getCreditByUser(@PathVariable String userId) {
        Customer customer = customerService.findById(userId);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.findSystemCreditByCustomer(customer)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> updateCredit(@PathVariable String creditId, @Valid @RequestBody WritableCredit writableCredit) {
        Credit credit = CreditMapper.writableCreditToCredit(writableCredit);
        credit.setCreditType(CreditType.SYSTEM_CREDIT);
        activityService.populator(credit.getCustomer(), null, BigDecimal.ZERO, BigDecimal.ZERO, credit.getTotalDebt(), credit.getCreditLimit().subtract(credit.getTotalDebt()), credit.getCreditLimit(), PaymentType.RUNNING_ACCOUNT, ActivityType.SYSTEM_CREDIT);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.update(creditId, credit)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> deleteCredit(@PathVariable String creditId) {
        Credit systemCredit = creditService.findById(creditId);
        creditService.delete(systemCredit);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(systemCredit));
    }

    @GetMapping("/activities")
    public ResponseEntity<WrapperPagination<ReadableActivity>> getActivities(@RequestParam(required = false) String userId, @RequestParam(required = false) String userName, @RequestParam(required = false) ActivityType activityType, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate lastDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        SearchSpecificationBuilder<Activity> searchBuilder = new SearchSpecificationBuilder<>();

        if (userId != null && !userId.isEmpty()) {
            User user = userService.findById(userId);
            searchBuilder.add("customer", SearchOperations.EQUAL, user, false);
        } else if (userName != null && !userName.isEmpty()) {
            User user = userService.findByUserName(userName);
            searchBuilder.add("customer", SearchOperations.EQUAL, user, false);
        }

        if (activityType != null) {
            searchBuilder.add("activityType", SearchOperations.EQUAL, activityType, false);
        }

        if (startDate != null) {
            searchBuilder.add("date", SearchOperations.GREATER_THAN, startDate, false);
            if (lastDate != null) {
                searchBuilder.add("date", SearchOperations.LESS_THAN, lastDate, false);
            }
        }

        return ResponseEntity.ok(CreditMapper.pagedActivityListToWrapperReadableActivity(activityService.findAllBySpecification(searchBuilder.build(), pageNumber, sortBy, sortType)));
    }
}
