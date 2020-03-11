package com.marketing.web.controllers.admin;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.ReadableCreditActivity;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.enums.CreditType;
import com.marketing.web.models.Credit;
import com.marketing.web.models.User;
import com.marketing.web.services.credit.CreditActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.user.UserService;
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
    public ResponseEntity<WrapperPagination<ReadableCreditActivity>> getCreditActivities(@RequestParam(required = false) String userId, @RequestParam(required = false) String userName, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date lastDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        User user = null;
        if (userId != null && !userId.isEmpty()) {
            user = userService.findByUUID(userId);
        } else if (userName != null && !userName.isEmpty()) {
            user = userService.findByUserName(userName);
        }

        if(user != null) {
            if(startDate != null && startDate.compareTo(new Date()) < 0) {
                if (lastDate == null) {
                    lastDate = new Date();
                }
                return ResponseEntity.ok(CreditMapper.pagedCreditActivityListToWrapperReadableCredityActivity(creditActivityService.findAllByUserAndDateRange(user, startDate, lastDate, pageNumber, sortBy, sortType)));
            }
            return ResponseEntity.ok(CreditMapper.pagedCreditActivityListToWrapperReadableCredityActivity(creditActivityService.findAllByUser(user,pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(CreditMapper.pagedCreditActivityListToWrapperReadableCredityActivity(creditActivityService.findAll(pageNumber, sortBy, sortType)));
    }
}
