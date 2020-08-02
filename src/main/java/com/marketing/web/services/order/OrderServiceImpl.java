package com.marketing.web.services.order;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.dtos.order.OrderItemPDF;
import com.marketing.web.dtos.order.OrderSummary;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.repositories.OrderGroup;
import com.marketing.web.repositories.OrderRepository;
import com.marketing.web.utils.PdfGenerator;
import com.marketing.web.utils.mappers.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final PdfGenerator pdfGenerator;

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository, PdfGenerator pdfGenerator) {
        this.orderRepository = orderRepository;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public Page<Order> findAll(int pageNumber, String sortBy, String sortType){
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Order> resultPage = orderRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<Order> findAll() {
       return orderRepository.findAll();
    }

    @Override
    public OrderSummary groupBy(Merchant merchant) {
        List<OrderGroup> orderGroups = orderRepository.groupBy(merchant);
        OrderSummary orderSummary = new OrderSummary();
        for (OrderGroup orderGroup : orderGroups) {
                switch (Objects.requireNonNull(OrderStatus.fromValue(orderGroup.getStatus()))) {
                    case NEW:
                        orderSummary.setNewCount(orderGroup.getCnt().intValue());
                        break;
                    case FINISHED:
                        orderSummary.setFinishedCount(orderGroup.getCnt().intValue());
                        break;
                    case CANCEL_REQUEST:
                        orderSummary.setCancelRequestCount(orderGroup.getCnt().intValue());
                        break;
                    case CANCELLED:
                        orderSummary.setCancelledCount(orderGroup.getCnt().intValue());
                        break;
                    case CONFIRMED:
                        orderSummary.setSubmittedCount(orderGroup.getCnt().intValue());
                }
        }
        orderSummary.setId("ordersummary".hashCode() + merchant.getId().toString());
        return orderSummary;
    }

    @Override
    public Page<Order> findAllBySpecification(Specification<Order> specification, Integer pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Order> resultPage = orderRepository.findAll(specification, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<Order> findAllBySpecification(Specification<Order> specification) {
        return orderRepository.findAll(specification);
    }

    @Override
    public Order findById(String id) {
        return orderRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order", id.toString()));
    }

    @Override
    public List<Order> createAll(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }

    @Override
    public Order findByIdAndMerchant(String id, Merchant merchant) {
        return orderRepository.findByMerchantAndId(merchant, UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order",id));
    }

    @Override
    public Order findByIdAndCustomer(String id, Customer customer) {
        return orderRepository.findByCustomerAndId(customer, UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order", id));
    }

    @Override
    public Order update(String id, Order updatedOrder) {
        Order order = findById(id);
        order.setWaybillDate(updatedOrder.getWaybillDate());
        order.setStatus(updatedOrder.getStatus());
        order.setCommentable(updatedOrder.isCommentable());
        return orderRepository.save(order);
    }

    @Override
    public byte[] orderToPDF(Order order) {
        List<OrderItemPDF> orderItemPDFList = order.getOrderItems().stream().map(OrderMapper::orderItemToOrderItemPDF).collect(Collectors.toList());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId",order.getId().toString());
        parameters.put("totalOrderPrice", order.getOrderItems().stream().map(OrderItem::getDiscountedTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        parameters.put("merchantName", order.getMerchant().getUser().getName());
        parameters.put("customerName", order.getCustomer().getUser().getName());
        parameters.put("customerPhone", order.getCustomer().getUser().getPhoneNumber());
        parameters.put("customerAddress", order.getCustomer().getUser().getAddressDetails());
        parameters.put("customerState", order.getCustomer().getUser().getState().getTitle());
        parameters.put("customerCity", order.getCustomer().getUser().getCity().getTitle());
        parameters.put("orderDate", Date.from(order.getOrderDate().atStartOfDay(ZoneId.of("Europe/Istanbul")).toInstant()));
        return pdfGenerator.generateJasperPDF("order-report.jrxml", orderItemPDFList, parameters);
    }

    @Override
    public byte[] orderToExcel(Order order) {
        return new byte[0];
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
