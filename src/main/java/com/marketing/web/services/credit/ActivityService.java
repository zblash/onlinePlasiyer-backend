package com.marketing.web.services.credit;

import com.marketing.web.enums.ActivityType;
import com.marketing.web.enums.PaymentType;
import com.marketing.web.models.Activity;
import com.marketing.web.models.Customer;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public interface ActivityService {

    Page<Activity> findAll(int pageNumber, String sortBy, String sortType);

    Page<Activity> findAllBySpecification(Specification<Activity> specification, Integer pageNumber, String sortBy, String sortType);

    Activity findById(String id);

    Activity create(Activity activity);

    Activity update(String id, Activity updatedActivity);

    void delete(Activity activity);

    void saveAll(List<Activity> activities);

    Activity populator(Customer customer, Merchant merchant, BigDecimal paidPrice, BigDecimal price, BigDecimal currentDebt, BigDecimal currentReceivable, BigDecimal creditLimit, PaymentType paymentType, ActivityType activityType);

}
