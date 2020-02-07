package com.marketing.web.services.invoice;

import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;


public interface ObligationService {

    Page<Obligation> findAll(int pageNumber, String sortBy, String sortType);

    Obligation findById(Long id);

    Obligation findByUuid(String uuid);

    Page<Obligation> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    ReadableTotalObligation getTotalObligationByUser(User user);

    Obligation create(Obligation obligation);

    List<Obligation> createAll(List<Obligation> obligations);

    Obligation update(String uuid, Obligation updatedObligation);

    void delete(Obligation obligation);
}
