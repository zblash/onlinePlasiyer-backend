package com.marketing.web.services.invoice;

import com.marketing.web.models.Merchant;
import com.marketing.web.models.Obligation;
import org.springframework.data.domain.Page;

import java.util.List;


public interface ObligationService {

    Page<Obligation> findAll(int pageNumber, String sortBy, String sortType);

    Obligation findById(String id);

    Obligation findByMerchant(Merchant merchant);

    Obligation create(Obligation obligation);

    List<Obligation> createAll(List<Obligation> obligations);

    Obligation update(String id, Obligation updatedObligation);

    void delete(Obligation obligation);
}
