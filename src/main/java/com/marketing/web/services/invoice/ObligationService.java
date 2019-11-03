package com.marketing.web.services.invoice;

import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ObligationService {

    Page<Obligation> findAll(int pageNumber);

    List<Obligation> findAllByUser(User user);

    Obligation findById(Long id);

    Obligation findByUuid(String uuid);

    Page<Obligation> findAllByUser(User user, int pageNumber);

    Obligation create(Obligation obligation);

    Obligation update(String uuid, Obligation updatedObligation);

    void delete(Obligation obligation);
}
