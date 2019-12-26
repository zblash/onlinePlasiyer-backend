package com.marketing.web.services.credit;

import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;

public interface CreditService {

    Page<Credit> findAll(int pageNumber, String sortBy, String sortType);

    Credit findById(Long id);

    Credit findByUUID(String uuid);

    Credit findByUser(Long userId);

    Credit create(Credit credit);

    Credit update(String uuid, Credit updatedCredit);

    void delete(Credit credit);
}
