package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.*;
import com.marketing.web.enums.*;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.credit.ActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.user.CustomerService;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.user.RoleService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.specifications.SearchSpecificationBuilder;
import com.marketing.web.utils.mappers.CreditMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/private/credits")
public class CreditsController {

    private final CreditService creditService;

    private final ActivityService activityService;

    private final UserService userService;

    private final MerchantService merchantService;

    private final CustomerService customerService;

    private final RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(CreditsController.class);

    public CreditsController(CreditService creditService, ActivityService activityService, UserService userService, MerchantService merchantService, CustomerService customerService, RoleService roleService) {
        this.creditService = creditService;
        this.activityService = activityService;
        this.userService = userService;
        this.merchantService = merchantService;
        this.customerService = customerService;
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<ReadableCredit> getUserCredit() {
        Customer customer = customerService.getLoggedInCustomer();
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.findSystemCreditByCustomer(customer)));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MERCHANT')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableUsersCredit>> getAllCredits(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalDebt") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        RoleType roleType = roleService.getLoggedInUserRole();
        if (roleType.equals(RoleType.MERCHANT)) {
            return ResponseEntity.ok(CreditMapper.pagedUsersCreditListToWrapperReadableUsersCredit(creditService.findAllByMerchant(merchantService.getLoggedInMerchant(), pageNumber, sortBy, sortType)));
        } else {
            return ResponseEntity.ok(CreditMapper.pagedUsersCreditListToWrapperReadableUsersCredit(creditService.findAllByCustomer(customerService.getLoggedInCustomer(), pageNumber, sortBy, sortType)));
        }


    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MERCHANT')")
    @GetMapping("/{userId}")
    public ResponseEntity<ReadableUsersCredit> getByUser(@PathVariable String userId) {
        RoleType roleType = roleService.getLoggedInUserRole();
        Merchant merchant = roleType.equals(RoleType.MERCHANT) ? merchantService.getLoggedInMerchant() : merchantService.findById(userId);
        Customer customer = roleType.equals(RoleType.CUSTOMER) ? customerService.getLoggedInCustomer() : customerService.findById(userId);

        Optional<Credit> optionalCredit = creditService.findByCustomerAndMerchant(customer, merchant);

        if (optionalCredit.isPresent()) {
            return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(optionalCredit.get()));
        }
        throw new BadRequestException("There is no credit");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping
    public ResponseEntity<ReadableUsersCredit> createCredit(@RequestBody WritableUserCredit writableUserCredit) {
        Merchant merchant = merchantService.getLoggedInMerchant();
        Customer customer = customerService.findById(writableUserCredit.getCustomerId());
        Credit credit = new Credit();
        credit.setCreditLimit(writableUserCredit.getCreditLimit());
        credit.setCustomer(customer);
        credit.setMerchant(merchant);
        credit.setTotalDebt(writableUserCredit.getTotalDebt());
        credit.setCreditType(CreditType.MERCHANT_CREDIT);

        activityService.populator(customer, merchant, BigDecimal.ZERO, BigDecimal.ZERO, credit.getTotalDebt(), credit.getCreditLimit().subtract(credit.getTotalDebt()), credit.getCreditLimit(), PaymentType.RUNNING_ACCOUNT, ActivityType.MERCHANT_CREDIT);
        return new ResponseEntity<>(CreditMapper.usersCreditToReadableUsersCredit(creditService.create(credit)), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableUsersCredit> updateCredit(@PathVariable String id, @RequestBody WritableUserCredit writableUserCredit) {
        Merchant merchant = merchantService.getLoggedInMerchant();
        Customer customer = customerService.findById(writableUserCredit.getCustomerId());
        Credit credit = creditService.findByUUIDAndMerchant(id, merchant);
        credit.setCreditLimit(writableUserCredit.getCreditLimit());
        credit.setCustomer(customer);
        credit.setTotalDebt(writableUserCredit.getTotalDebt());
        activityService.populator(customer, merchant, BigDecimal.ZERO, BigDecimal.ZERO, credit.getTotalDebt(), credit.getCreditLimit().subtract(credit.getTotalDebt()), credit.getCreditLimit(), PaymentType.RUNNING_ACCOUNT, ActivityType.MERCHANT_CREDIT);
        return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(creditService.update(credit.getId().toString(), credit)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableUsersCredit> deleteCredit(@PathVariable String id) {
        User loggedInUser = userService.getLoggedInUser();
        Credit credit;
        if (UserMapper.roleToRoleType(loggedInUser.getRole()).equals(RoleType.ADMIN)) {
            credit = creditService.findById(id);
        } else {
            credit = creditService.findByUUIDAndMerchant(id, merchantService.findByUser(loggedInUser));
        }
        creditService.delete(credit);
        return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(credit));
    }

    @GetMapping("/activities")
    public ResponseEntity<WrapperPagination<ReadableActivity>> getActivities(@RequestParam(required = false) String customerId, @RequestParam(required = false) String merchantId, @RequestParam(required = false) ActivityType activityType, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate lastDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        RoleType roleType = roleService.getLoggedInUserRole();
        SearchSpecificationBuilder<Activity> searchBuilder = new SearchSpecificationBuilder<>();

        if (roleType.equals(RoleType.MERCHANT)) {
            Merchant merchant = merchantService.getLoggedInMerchant();
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchant, false);
            addCustomerToSearch(searchBuilder, customerId);
        } else if (roleType.equals(RoleType.CUSTOMER)) {
            Customer customer = customerService.getLoggedInCustomer();
            searchBuilder.add("customer", SearchOperations.EQUAL, customer, false);
            addMerchantToSearch(searchBuilder, merchantId);
        } else {
            addCustomerToSearch(searchBuilder, customerId);
            addMerchantToSearch(searchBuilder, merchantId);
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

    private void addMerchantToSearch(SearchSpecificationBuilder<Activity> searchBuilder, String merchantId) {
        if (merchantId != null && !merchantId.isEmpty()) {
            Merchant merchant = merchantService.findById(merchantId);
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchant, false);
        }
    }

    private void addCustomerToSearch(SearchSpecificationBuilder<Activity> searchBuilder, String customerId) {
        if (customerId != null && !customerId.isEmpty()) {
            Customer customer = customerService.findById(customerId);
            searchBuilder.add("customer", SearchOperations.EQUAL, customer, false);
        }
    }
}
