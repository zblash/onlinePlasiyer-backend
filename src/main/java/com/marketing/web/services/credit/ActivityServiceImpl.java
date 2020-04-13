package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Activity;
import com.marketing.web.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;


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
    public Activity findById(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",id.toString()));

    }

    @Override
    public Activity findByUUID(String uuid) {
        return activityRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",uuid));
    }

    @Override
    public Activity create(Activity activity) {
        activity.setDate(LocalDate.now());
        return activityRepository.save(activity);
    }

    @Override
    public Activity update(String uuid, Activity updatedActivity) {
        Activity activity = findByUUID(uuid);
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

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}