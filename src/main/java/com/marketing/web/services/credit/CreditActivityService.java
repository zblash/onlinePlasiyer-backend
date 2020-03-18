package com.marketing.web.services.credit;

import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CreditActivityService {

    Page<CreditActivity> findAll(int pageNumber, String sortBy, String sortType);

    Page<CreditActivity> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    CreditActivity findById(Long id);

    CreditActivity findByUUID(String uuid);

    List<CreditActivity> findAllByOrder(Order order);

    CreditActivity create(CreditActivity creditActivity);

    CreditActivity update(String uuid, CreditActivity updatedCreditActivity);

    void delete(CreditActivity creditActivity);

    void deleteByOrder(Order order);

    void saveAll(List<CreditActivity> creditActivities);

    Page<CreditActivity> findAllBySpecification(Specification<CreditActivity> specification, Integer pageNumber, String sortBy, String sortType);
}
