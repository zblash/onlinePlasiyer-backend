package com.marketing.web.services.invoice;

import com.marketing.web.models.Obligation;
import com.marketing.web.models.ObligationActivity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ObligationActivityService {

    Page<ObligationActivity> findAll(int pageNumber, String sortBy, String sortType);

    Page<ObligationActivity> findAllByObligation(Obligation obligation, int pageNumber, String sortBy, String sortType);

    ObligationActivity findById(Long id);

    ObligationActivity findByUuid(String uuid);

    ObligationActivity create(ObligationActivity obligationActivity);

    ObligationActivity update(String uuid, ObligationActivity updatedObligationActivity);

    List<ObligationActivity> saveAll(List<ObligationActivity> obligationActivities);

    void deleteAll(List<ObligationActivity> obligationActivities);

    void delete(ObligationActivity obligationActivity);

}
