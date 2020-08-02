package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.ActivityType;
import com.marketing.web.enums.PaymentType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Activity;
import com.marketing.web.models.Customer;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;
import com.marketing.web.repositories.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Page<Activity> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Activity> resultPage = activityRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Activity> findAllBySpecification(Specification<Activity> specification, Integer pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Activity> resultPage = activityRepository.findAll(specification, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Activity findById(String id) {
        return activityRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",id.toString()));

    }

    @Override
    public Activity create(Activity activity) {
        activity.setDate(LocalDate.now());
        return activityRepository.save(activity);
    }

    @Override
    public Activity update(String id, Activity updatedActivity) {
        Activity activity = findById(id);
        activity.setActivityType(updatedActivity.getActivityType());
        activity.setCreditLimit(updatedActivity.getCreditLimit());
        activity.setCurrentDebt(updatedActivity.getCurrentDebt());
        activity.setCurrentReceivable(updatedActivity.getCurrentReceivable());
        activity.setCustomer(updatedActivity.getCustomer());
        activity.setMerchant(updatedActivity.getMerchant());
        activity.setPrice(updatedActivity.getPrice());
        activity.setPaymentType(updatedActivity.getPaymentType());
        activity.setDate(updatedActivity.getDate());
        return activityRepository.save(activity);
    }

    @Override
    public void delete(Activity activity) {
        activityRepository.delete(activity);
    }

    @Override
    public void saveAll(List<Activity> activities) {
        activityRepository.saveAll(activities);
    }

    @Override
    public Activity populator(Customer customer, Merchant merchant, BigDecimal paidPrice, BigDecimal price, BigDecimal currentDebt, BigDecimal currentReceivable, BigDecimal creditLimit, PaymentType paymentType, ActivityType activityType) {
        return Activity.builder().activityType(activityType).paymentType(paymentType)
                .customer(customer).merchant(merchant)
                .paidPrice(paidPrice)
                .price(price).creditLimit(creditLimit).currentDebt(currentDebt).currentReceivable(currentReceivable)
                .build();
    }
    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
