package com.marketing.web.services.credit;

import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CreditActivityService {

    Page<CreditActivity> findAll(int pageNumber, String sortBy, String sortType);

    CreditActivity findById(Long id);

    CreditActivity findByUUID(String uuid);

    List<CreditActivity> findAllByCredit(Credit credit);

    List<CreditActivity> findAllByOrder(Order order);

    CreditActivity create(CreditActivity creditActivity);

    CreditActivity update(String uuid, CreditActivity updatedCreditActivity);

    void delete(CreditActivity creditActivity);

    void deleteByOrder(Order order);

}
