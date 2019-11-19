package com.marketing.web.services.invoice;

import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;


public interface ObligationService {

    Page<Obligation> findAll(int pageNumber);

    Obligation findById(Long id);

    Obligation findByUuid(String uuid);

    Page<Obligation> findAllByUser(User user, int pageNumber);

    ReadableTotalObligation getTotalObligationByUser(User user);

    Obligation create(Obligation obligation);

    Obligation update(String uuid, Obligation updatedObligation);

    void delete(Obligation obligation);
}
