package com.marketing.web.services.invoice;

import com.marketing.web.models.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ObligationActivityService {

    Page<ObligationActivity> findAll(int pageNumber, String sortBy, String sortType);

    Page<ObligationActivity> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType);

    ObligationActivity findByOrder(Order order);

    ObligationActivity findById(String id);

    ObligationActivity create(ObligationActivity obligationActivity);

    ObligationActivity update(String id, ObligationActivity updatedObligationActivity);

    List<ObligationActivity> saveAll(List<ObligationActivity> obligationActivities);

    void deleteAll(List<ObligationActivity> obligationActivities);

    void delete(ObligationActivity obligationActivity);

    ObligationActivity populator(Obligation obligation, Order order);

}
