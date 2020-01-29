package com.marketing.web.services.credit;

import com.marketing.web.models.SystemCredit;
import org.springframework.data.domain.Page;

public interface SystemCreditService {

    Page<SystemCredit> findAll(int pageNumber, String sortBy, String sortType);

    SystemCredit findById(Long id);

    SystemCredit findByUUID(String uuid);

    SystemCredit findByUser(Long userId);

    SystemCredit create(SystemCredit systemCredit);

    SystemCredit update(String uuid, SystemCredit updatedSystemCredit);

    void delete(SystemCredit systemCredit);
}
