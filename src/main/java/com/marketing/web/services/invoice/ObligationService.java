package com.marketing.web.services.invoice;

import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

public interface ObligationService {

    Page<Obligation> findAll(int pageNumber);

    Obligation findById(Long id);

    Page<Obligation> findAllByUser(User user, int pageNumber);

    Obligation create(Obligation obligation);

    Obligation update(Long id, Obligation updatedObligation);

    void delete(Obligation obligation);
}
