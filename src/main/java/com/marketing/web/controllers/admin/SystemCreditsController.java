package com.marketing.web.controllers.admin;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.ReadableCreditActivity;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.SearchOperations;
import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.User;
import com.marketing.web.services.credit.CreditActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.specifications.SearchSpecificationBuilder;
import com.marketing.web.utils.mappers.CreditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/api/admin/credits")
public class SystemCreditsController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private CreditActivityService creditActivityService;

    @Autowired
    private UserService userService;

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
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<ReadableCredit> getCreditByUser(@PathVariable String userId) {
        User user = userService.findByUUID(userId);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.findSystemCreditByUser(user)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> updateCredit(@PathVariable String creditId, @Valid @RequestBody WritableCredit writableCredit) {
        Credit credit = CreditMapper.writableCreditToCredit(writableCredit);
        credit.setCreditType(CreditType.SYSTEM_CREDIT);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.update(creditId, credit)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> deleteCategory(@PathVariable String creditId) {
        Credit systemCredit = creditService.findByUUID(creditId);
        creditService.delete(systemCredit);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(systemCredit));
    }

    @GetMapping("/activities")
    public ResponseEntity<WrapperPagination<ReadableCreditActivity>> getCreditActivities(@RequestParam(required = false) String creditId, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate lastDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {

        SearchSpecificationBuilder<CreditActivity> searchBuilder = new SearchSpecificationBuilder<>();

        if (creditId != null && !creditId.isEmpty()) {
            Credit credit = creditService.findByUUID(creditId);
            searchBuilder.add("credit", SearchOperations.EQUAL, credit,false);
        }

        if (startDate != null) {
            searchBuilder.add("date", SearchOperations.EQUAL, startDate, false);
            if (lastDate != null) {
                searchBuilder.add("date", SearchOperations.LESS_THAN, lastDate, false);
            }
        }

        return ResponseEntity.ok(CreditMapper.pagedCreditActivityListToWrapperReadableCredityActivity(creditActivityService.findAllBySpecification(searchBuilder.build(), pageNumber, sortBy, sortType)));
    }
}
