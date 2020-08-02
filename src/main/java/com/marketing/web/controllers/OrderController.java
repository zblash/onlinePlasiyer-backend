package com.marketing.web.controllers;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.order.*;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.SearchOperations;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.repositories.MerchantScoreRepository;
import com.marketing.web.services.order.OrderItemService;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.user.CustomerService;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.user.RoleService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.specifications.SearchSpecificationBuilder;
import com.marketing.web.utils.PdfGenerator;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/private/orders")
public class OrderController {

    private final OrderService orderService;

    private final OrderItemService orderItemService;

    private final UserService userService;

    private final OrderFacade orderFacade;

    private final MerchantService merchantService;

    private final CustomerService customerService;

    private final MerchantScoreRepository merchantScoreRepository;

    private final RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService, OrderItemService orderItemService, UserService userService, OrderFacade orderFacade, MerchantService merchantService, CustomerService customerService, MerchantScoreRepository merchantScoreRepository, RoleService roleService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
        this.orderFacade = orderFacade;
        this.merchantService = merchantService;
        this.customerService = customerService;
        this.merchantScoreRepository = merchantScoreRepository;
        this.roleService = roleService;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping("/report/pdf/{id}")
    public ResponseEntity<?> generateOrderReportPDF(@PathVariable String id) {
        RoleType roleType = roleService.getLoggedInUserRole();
        SearchSpecificationBuilder<Order> searchBuilder = new SearchSpecificationBuilder<>();
        if (roleType.equals(RoleType.CUSTOMER)) {
            searchBuilder.add("customer", SearchOperations.EQUAL, customerService.getLoggedInCustomer(), false);
        } else if (roleType.equals(RoleType.MERCHANT)) {
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchantService.getLoggedInMerchant(), false);
        }
        searchBuilder.add("id", SearchOperations.EQUAL, UUID.fromString(id), false);
        Optional<Order> optionalOrder = orderService.findAllBySpecification(searchBuilder.build()).stream().findFirst();
        if (optionalOrder.isPresent()) {
            byte[] orderPDF = orderService.orderToPDF(optionalOrder.get());
            HttpHeaders headers = new HttpHeaders();
            String fileName = "inline; order"+optionalOrder.get().getId().toString()+".pdf";
            headers.add("Content-Disposition", fileName);
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(orderPDF);
        }
        throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order",id);
    }
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableOrder>> getOrders(@RequestParam(required = false) OrderStatus status, @RequestParam(required = false) String customerId, @RequestParam(required = false) String customerName, @RequestParam(required = false) String merchantId, @RequestParam(required = false) String merchantName, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        SearchSpecificationBuilder<Order> searchBuilder = new SearchSpecificationBuilder<>();
        RoleType roleType = roleService.getLoggedInUserRole();

        if (roleType.equals(RoleType.MERCHANT)) {
            Merchant merchant = merchantService.getLoggedInMerchant();
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchant, false);
            addCustomerToSearch(searchBuilder, customerId, customerName);
        } else if (roleType.equals(RoleType.CUSTOMER)) {
            Customer customer = customerService.getLoggedInCustomer();
            searchBuilder.add("customer", SearchOperations.EQUAL, customer, false);
            addMerchantToSearch(searchBuilder, merchantId, merchantName);
        } else {
            addCustomerToSearch(searchBuilder, customerId, customerName);
            addMerchantToSearch(searchBuilder, merchantId, merchantName);
        }

        if (startDate != null) {
            searchBuilder.add("orderDate", SearchOperations.GREATER_THAN, startDate, false);
            if (endDate != null) {
                searchBuilder.add("orderDate", SearchOperations.LESS_THAN, endDate, false);
            }
        }

        if (status != null) {
            searchBuilder.add("status", SearchOperations.EQUAL, status, false);
        }

        return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllBySpecification(searchBuilder.build(), pageNumber, sortBy, sortType)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/summary")
    public ResponseEntity<OrderSummary> getOrderSummary() {
        return ResponseEntity.ok(orderService.groupBy(merchantService.getLoggedInMerchant()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/summary/byUser/{userId}")
    public ResponseEntity<OrderSummary> getUserOrderSummary(@PathVariable String userId) {
        Merchant merchant = merchantService.findById(userId);
        return ResponseEntity.ok(orderService.groupBy(merchant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableOrder> getUserOrder(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(OrderMapper.orderToReadableOrder(getOrder(user, id)));
    }

    @PostMapping("/items/{id}")
    public ResponseEntity<List<ReadableOrderItem>> getUserOrderDetails(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        Order order = getOrder(user, id);
        return ResponseEntity.ok(orderItemService.findByOrder(order).stream()
                .map(OrderMapper::orderItemToReadableOrderItem).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableOrder> updateOrder(@PathVariable String id, @Valid @RequestBody WritableOrder writableOrder) {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT) && writableOrder.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new BadRequestException("You can not cancel order directly");
        }
        ReadableOrder readableOrder = orderFacade.saveOrder(writableOrder, getOrder(user, id));
        return ResponseEntity.ok(readableOrder);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("confirm/{id}")
    public ResponseEntity<ReadableOrder> confirmOrder(@PathVariable String id, @Valid @RequestBody WritableConfirmOrder writableConfirmOrder) {
        User user = userService.getLoggedInUser();
        Order order = getOrder(user, id);
        return ResponseEntity.ok(orderFacade.confirmOrder(writableConfirmOrder, order));
    }

    private Order getOrder(User user, String id) {
        RoleType roleType = UserMapper.roleToRoleType(user.getRole());
        switch (roleType) {
            case ADMIN:
                return orderService.findById(id);
            case CUSTOMER:
                return orderService.findByIdAndCustomer(id, customerService.findByUser(user));
            case MERCHANT:
                return orderService.findByIdAndMerchant(id, merchantService.findByUser(user));
            default:
                return null;
        }
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/comment/{id}")
    public ResponseEntity<ReadableOrder> addCommentToOrder(@PathVariable String id, @RequestBody @NotNull Map<String, Float> body) {
        Customer customer = customerService.getLoggedInCustomer();
        Order order = orderService.findByIdAndCustomer(id, customer);
        if (OrderStatus.FINISHED.equals(order.getStatus()) && order.isCommentable()) {
            order.setCommentable(false);
            orderService.update(order.getId().toString(), order);
            MerchantScore merchantScore = new MerchantScore();
            MerchantScoreComposite merchantScoreComposite = new MerchantScoreComposite();
            merchantScoreComposite.setMerchant(order.getMerchant());
            merchantScoreComposite.setOrder(order);
            merchantScore.setScore(body.get("score"));
            merchantScore.setMerchantScoreComposite(merchantScoreComposite);
            merchantScoreRepository.save(merchantScore);
            return ResponseEntity.ok(OrderMapper.orderToReadableOrder(order));
        }
        throw new BadRequestException("Order is not commentable");
    }



    private void addMerchantToSearch(SearchSpecificationBuilder<Order> searchBuilder, String merchantId, String merchantName) {
        if (merchantId != null && !merchantId.isEmpty()) {
            Merchant merchant = merchantService.findById(merchantId);
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchant, false);
        } else if (merchantName != null && !merchantName.isEmpty()) {
            Merchant merchant = merchantService.findByUser(userService.findByUserName(merchantName));
            searchBuilder.add("merchant", SearchOperations.EQUAL, merchant, false);
        }
    }

    private void addCustomerToSearch(SearchSpecificationBuilder<Order> searchBuilder, String customerId, String customerName) {
        if (customerId != null && !customerId.isEmpty()) {
            Customer customer = customerService.findById(customerId);
            searchBuilder.add("customer", SearchOperations.EQUAL, customer, false);
        } else if (customerName != null && !customerName.isEmpty()) {
            Customer customer = customerService.findByUser(userService.findByUserName(customerName));
            searchBuilder.add("customer", SearchOperations.EQUAL, customer, false);
        }
    }
}
