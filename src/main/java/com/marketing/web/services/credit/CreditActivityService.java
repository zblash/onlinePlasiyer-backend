package com.marketing.web.services.credit;

import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface CreditActivityService {

    Page<CreditActivity> findAll(int pageNumber, String sortBy, String sortType);

    Page<CreditActivity> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    CreditActivity findById(Long id);

    CreditActivity findByUUID(String uuid);

    List<CreditActivity> findAllByCredit(Credit credit);

    List<CreditActivity> findAllByOrder(Order order);

    CreditActivity create(CreditActivity creditActivity);

    CreditActivity update(String uuid, CreditActivity updatedCreditActivity);

    void delete(CreditActivity creditActivity);

    void deleteByOrder(Order order);

    void saveAll(List<CreditActivity> creditActivities);

    Page<CreditActivity> findAllByUsers(User user1, User user2, Integer pageNumber, String sortBy, String sortType);

    Page<CreditActivity> findAllByUserAndDateRange(User user, Date startDate, Date lastDate, Integer pageNumber, String sortBy, String sortType);
}
