package com.marketing.web.services.invoice;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.repositories.ObligationActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ObligationActivityServiceImpl implements ObligationActivityService {

    @Autowired
    private ObligationActivityRepository obligationActivityRepository;

    @Override
    public Page<ObligationActivity> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ObligationActivity> resultPage = obligationActivityRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<ObligationActivity> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ObligationActivity> resultPage = obligationActivityRepository.findAllByMerchant(merchant, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public ObligationActivity findByOrder(Order order) {
        return obligationActivityRepository.findByOrder(order).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"obligation", ""));
    }

    @Override
    public ObligationActivity findById(String id) {
        return obligationActivityRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"obligation", id.toString()));
    }

    @Override
    public ObligationActivity create(ObligationActivity obligationActivity) {
        return obligationActivityRepository.save(obligationActivity);
    }

    @Override
    public ObligationActivity update(String id, ObligationActivity updatedObligationActivity) {
        ObligationActivity obligationActivity = findById(id);
        obligationActivity.setPriceValue(updatedObligationActivity.getPriceValue());
        obligationActivity.setTotalReceivable(updatedObligationActivity.getTotalReceivable());
        obligationActivity.setTotalDebt(updatedObligationActivity.getTotalDebt());
        obligationActivity.setCreditActivityType(updatedObligationActivity.getCreditActivityType());
        obligationActivity.setDate(updatedObligationActivity.getDate());
        obligationActivity.setOrder(updatedObligationActivity.getOrder());
        obligationActivity.setMerchant(updatedObligationActivity.getMerchant());
        return obligationActivity;
    }

    @Override
    public List<ObligationActivity> saveAll(List<ObligationActivity> obligationActivities) {
        return obligationActivityRepository.saveAll(obligationActivities);
    }

    @Override
    public void deleteAll(List<ObligationActivity> obligationActivities) {
        obligationActivityRepository.deleteAll(obligationActivities);
    }

    @Override
    public void delete(ObligationActivity obligationActivity) {
        obligationActivityRepository.delete(obligationActivity);
    }

    @Override
    public ObligationActivity populator(Obligation obligation, Order order) {
        CreditActivityType obligationType = PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) ? CreditActivityType.CREDIT : CreditActivityType.DEBT;
        ObligationActivity obligationActivity = new ObligationActivity();
        double commission = order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum();
        boolean paymentType = order.getPaymentType().equals(PaymentOption.COD) || order.getPaymentType().equals(PaymentOption.MERCHANT_CREDIT);
        BigDecimal price = paymentType ? BigDecimal.valueOf(commission) : order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(BigDecimal.valueOf(commission));
        obligationActivity.setOrder(order);
        obligationActivity.setMerchant(order.getMerchant());
        obligationActivity.setTotalDebt(obligation.getDebt());
        obligationActivity.setTotalReceivable(obligation.getReceivable());
        obligationActivity.setPriceValue(price);
        obligationActivity.setCreditActivityType(obligationType);
        obligationActivity.setDate(new Date());
        return obligationActivity;
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
