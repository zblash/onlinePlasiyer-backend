package com.marketing.web.controllers;

import com.marketing.web.configs.constants.ApplicationContstants;
import com.marketing.web.dtos.announcement.ReadableAnnouncement;
import com.marketing.web.dtos.category.ReadableCategory;
import com.marketing.web.dtos.user.*;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.HttpMessage;
import com.marketing.web.models.*;
import com.marketing.web.configs.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.announcement.AnnouncementService;
import com.marketing.web.services.cart.CartServiceImpl;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.product.ProductService;
import com.marketing.web.services.user.CityService;
import com.marketing.web.services.user.StateService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.MailUtil;
import com.marketing.web.utils.RandomStringGenerator;
import com.marketing.web.utils.mappers.AnnouncementMapper;
import com.marketing.web.utils.mappers.CityMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody WritableLogin writableLogin, WebRequest request) {
        User userDetails = userService.findByUserName(writableLogin.getUsername());

        if (userService.loginControl(writableLogin.getUsername(), writableLogin.getPassword())) {
            Map<String, Object> body = new HashMap<>();
            body.put("role", userDetails.getRole().getName());
            body.put("userId", userDetails.getId());
            String jwt = JWTGenerator.generate(ApplicationContstants.JWT_SECRET, null, 86_400_000, body);

            ReadableAddress address = new ReadableAddress();
            address.setCityId(userDetails.getCity().getUuid().toString());
            address.setCityName(userDetails.getCity().getTitle());
            address.setStateId(userDetails.getState().getUuid().toString());
            address.setStateName(userDetails.getState().getTitle());
            address.setDetails(userDetails.getAddressDetails());

            ReadableLogin.LoginDTOBuilder loginDTOBuilder = new ReadableLogin.LoginDTOBuilder(jwt);
            loginDTOBuilder.email(userDetails.getEmail());
            loginDTOBuilder.name(userDetails.getName());
            loginDTOBuilder.userName(userDetails.getUsername());
            String role = userDetails.getRole().getName().split("_")[1];
            loginDTOBuilder.role(role);
            loginDTOBuilder.address(address);
            loginDTOBuilder.activeStates(userDetails.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
            ReadableLogin readableLogin = loginDTOBuilder
                    .build();
            return ResponseEntity.ok(readableLogin);
        }

        HttpMessage httpMessage = new HttpMessage(HttpStatus.UNAUTHORIZED);
        httpMessage.setMessage("Given username or password incorrect");
        httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, httpMessage.getStatus());

    }

    @PostMapping("/sign-up")
    public ResponseEntity<ReadableRegister> signUp(@Valid @RequestBody WritableRegister writableRegister) {
        User user = UserMapper.writableRegisterToUser(writableRegister);
        if (userService.canRegister(user)) {

            City city = cityService.findByUuid(writableRegister.getCityId());

            user.setStatus(true);
            user.setActivationToken(RandomStringGenerator.generateId());
            user.setCity(city);
            user.setState(stateService.findByUuidAndCity(writableRegister.getStateId(), city));

            User createdUser = userService.create(user, writableRegister.getRoleType());
            RoleType roleType = UserMapper.roleToRoleType(createdUser.getRole());
            if (roleType.equals(RoleType.CUSTOMER)) {
                Cart cart = cartService.create(createdUser);
                user.setCart(cart);
                Credit credit = new Credit();
                credit.setCustomer(createdUser);
                credit.setTotalDebt(0);
                credit.setCreditLimit(0);
                credit.setCreditType(CreditType.SYSTEM_CREDIT);
                creditService.create(credit);
            } else if (roleType.equals(RoleType.MERCHANT)) {
                Obligation obligation = new Obligation();
                obligation.setUser(createdUser);
                obligation.setReceivable(0);
                obligation.setReceivable(0);
                obligationService.create(obligation);
            }
            return ResponseEntity.ok(UserMapper.userToReadableRegister(createdUser));
        }
        throw new BadRequestException("Username or email already registered");
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<HttpMessage> forgottenPassword(@RequestBody WritableForgotPassword writableForgotPassword, WebRequest request, Locale locale) {
        User user;
        if (!writableForgotPassword.getEmail().isEmpty()) {
            user = userService.findByEmail(writableForgotPassword.getEmail());
        } else if (!writableForgotPassword.getUsername().isEmpty()) {
            user = userService.findByUserName(writableForgotPassword.getUsername());
        } else {
            throw new BadRequestException("One of username or email is required");
        }
        user.setPasswordResetToken(RandomStringGenerator.generateId());
        user.setResetTokenExpireTime(
                Date.from(LocalDateTime.now().plus(1, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant()));
        mailUtil.sendPasswordResetMail(user.getEmail(), user.getPasswordResetToken(), locale);
        userService.update(user.getId(), user);
        HttpMessage httpMessage = new HttpMessage(HttpStatus.OK);
        httpMessage.setMessage("Password reset token has been sent to to your email");
        httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
        return ResponseEntity.ok(httpMessage);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<HttpMessage> passwordReset(@Valid @RequestBody WritablePasswordReset writablePasswordReset, WebRequest request) {
        User user = userService.findByResetToken(writablePasswordReset.getToken());
        Date now = new Date();
        if (now.compareTo(user.getResetTokenExpireTime()) < 0 &&
                writablePasswordReset.getPassword().equals(writablePasswordReset.getPasswordConfirmation())) {
            userService.changePassword(user, writablePasswordReset.getPassword());
            HttpMessage httpMessage = new HttpMessage(HttpStatus.OK);
            httpMessage.setMessage("Password changed");
            httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
            return ResponseEntity.ok(httpMessage);
        }
        throw new BadRequestException("Fields not matching");
    }

    @PutMapping("/activation")
    public ResponseEntity<ReadableUserInfo> userActivation(@Valid @RequestBody WritableActivation writableActivation) {
        User user = userService.findByActivationToken(writableActivation.getActivationToken());
        user.setStatus(true);
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId(), user)));
    }

    @PostMapping("/api/user/changePassword")
    public ResponseEntity<HttpMessage> changeUserPassword(@Valid @RequestBody WritablePasswordChange writablePasswordReset, WebRequest request) {
        User user = userService.getLoggedInUser();

        if (writablePasswordReset.getPassword().equals(writablePasswordReset.getPasswordConfirmation())) {
            userService.changePassword(user, writablePasswordReset.getPassword());
            HttpMessage httpMessage = new HttpMessage(HttpStatus.OK);
            httpMessage.setMessage("Password changed");
            httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
            return ResponseEntity.ok(httpMessage);
        }
        throw new BadRequestException("Fields not matching");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/api/user/activeStates")
    public ResponseEntity<List<ReadableState>> addActiveState(@RequestBody List<String> states) {
        User user = userService.getLoggedInUser();
        List<State> stateList = stateService.findAllByUuids(states);
        List<State> addedList = user.getActiveStates();
        addedList.addAll(stateList);
        user.setActiveStates(addedList.stream().distinct().collect(Collectors.toList()));
        userService.update(user.getId(), user);
        return ResponseEntity.ok(addedList.stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/api/user/activeStates")
    public ResponseEntity<List<ReadableState>> getActiveStates() {
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(user.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/api/user/info")
    public ResponseEntity<ReadableUserInfo> getUserInfos() {
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
    }

    @PutMapping("/api/user/info")
    public ResponseEntity<ReadableUserInfo> updateUserInfo(@Valid @RequestBody WritableUserInfo writableUserInfo) {
        User user = userService.getLoggedInUser();
        if (writableUserInfo.getEmail().equals(user.getEmail()) || !userService.checkUserByEmail(writableUserInfo.getEmail())) {
            user.setName(writableUserInfo.getName());
            user.setEmail(writableUserInfo.getEmail());
            State state = stateService.findByUuid(writableUserInfo.getAddress().getStateId());
            City city = cityService.findByUuid(writableUserInfo.getAddress().getCityId());
            user.setState(state);
            user.setCity(city);
            user.setAddressDetails(writableUserInfo.getAddress().getDetails());
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId(), user)));
        }
        throw new BadRequestException("Email already registered");
    }

    @GetMapping("/api/merchants")
    public ResponseEntity<List<MerchantUser>> getAllMerchants() {
        User loggedInUser = userService.getLoggedInUser();
        List<User> users = userService.findAllByRoleAndStateAndStatus(RoleType.MERCHANT, loggedInUser.getState(), true);
        return ResponseEntity.ok(users.stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/customers")
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    public ResponseEntity<List<CustomerUser>> getAllCustomersByActiveStates() {
        User loggedInUser = userService.getLoggedInUser();

        return ResponseEntity.ok(userService.findAllByStatesAndRole(loggedInUser.getActiveStates(), RoleType.CUSTOMER).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/announcements")
    public ResponseEntity<List<ReadableAnnouncement>> getAll(){
        List<Announcement> announcements = announcementService.findAllActives(new Date());
        return ResponseEntity.ok(announcements.stream()
                .map(AnnouncementMapper::announcementToReadableAnnouncement).collect(Collectors.toList()));
    }
}
