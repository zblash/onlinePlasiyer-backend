package com.marketing.web.controllers;

import com.marketing.web.dtos.shippingDays.ReadableShippingDays;
import com.marketing.web.dtos.shippingDays.WritableShippingDays;
import com.marketing.web.dtos.user.readable.ReadableState;
import com.marketing.web.enums.DaysOfWeek;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.ShippingDays;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.services.shippingDays.ShippingDaysService;
import com.marketing.web.services.user.CustomerService;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.user.StateService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.CityMapper;
import com.marketing.web.utils.mappers.ShippingDaysMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/private/shippingDays")
public class ShippingDaysController {

    private Logger logger = LoggerFactory.getLogger(ShippingDaysController.class);

    private final ShippingDaysService shippingDaysService;

    private final CustomerService customerService;

    private final UserService userService;

    private final MerchantService merchantService;

    private final StateService stateService;

    public ShippingDaysController(ShippingDaysService shippingDaysService, CustomerService customerService, UserService userService, MerchantService merchantService, StateService stateService) {
        this.shippingDaysService = shippingDaysService;
        this.customerService = customerService;
        this.userService = userService;
        this.merchantService = merchantService;
        this.stateService = stateService;
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping
    public ResponseEntity<List<ReadableShippingDays>> getAllDays() {
        Merchant merchant = merchantService.getLoggedInMerchant();
        return ResponseEntity.ok(shippingDaysService.findAllByMerchant(merchant).stream().map(ShippingDaysMapper::shippingDaysToReadableShippingDays).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/allowedStates")
    public ResponseEntity<List<ReadableState>> getAllowedStateForCreate() {
        Merchant merchant = merchantService.getLoggedInMerchant();
        Set<State> stateSet = merchant.getActiveStates();
        List<ShippingDays> shippingDaysList = shippingDaysService.findAllByMerchant(merchant);
        stateSet.removeAll(shippingDaysList.stream().map(ShippingDays::getState).collect(Collectors.toList()));
        return ResponseEntity.ok(stateSet.stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/merchant/{id}")
    public ResponseEntity<ReadableShippingDays> getShippingDaysByMerchant(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        Merchant merchant = merchantService.findById(id);
        return ResponseEntity.ok(ShippingDaysMapper.shippingDaysToReadableShippingDays(shippingDaysService.findByMerchantAndState(merchant, user.getState())));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping
    public ResponseEntity<ReadableShippingDays> createShippingDays(@RequestBody @Valid WritableShippingDays writableShippingDays) {
        Merchant merchant = merchantService.getLoggedInMerchant();
        State state = stateService.findById(writableShippingDays.getStateId());
        if (!shippingDaysService.hasShippingDays(merchant, state)) {
            ShippingDays shippingDays = new ShippingDays();
            shippingDays.setMerchant(merchant);
            shippingDays.setDays(writableShippingDays.getDays());
            shippingDays.setState(state);
            return ResponseEntity.ok(ShippingDaysMapper.shippingDaysToReadableShippingDays(shippingDaysService.create(shippingDays)));
        }
        throw new BadRequestException("This state already has days");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableShippingDays> updateShippingDays(@PathVariable String id, @RequestBody Map<String, List<DaysOfWeek>> responseBody) {
        if (responseBody.get("days") != null) {
            Merchant merchant = merchantService.getLoggedInMerchant();
            ShippingDays shippingDays = shippingDaysService.findByMerchantAndId(merchant, id);
            shippingDays.setDays(responseBody.get("days"));
            return ResponseEntity.ok(ShippingDaysMapper.shippingDaysToReadableShippingDays(shippingDaysService.update(id, shippingDays)));
        }
        throw new BadRequestException("Request body object 'days' is required");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableShippingDays> deleteShippingDays(@PathVariable String id) {
        Merchant merchant = merchantService.getLoggedInMerchant();
        ShippingDays shippingDays = shippingDaysService.findByMerchantAndId(merchant, id);
        shippingDaysService.delete(shippingDays);
        return ResponseEntity.ok(ShippingDaysMapper.shippingDaysToReadableShippingDays(shippingDays));
    }

}
