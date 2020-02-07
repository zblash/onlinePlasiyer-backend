package com.marketing.web.services.credit;

import com.marketing.web.enums.CreditType;
import com.marketing.web.models.User;
import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CreditService {
    Page<Credit> findAll(int pageNumber, String sortBy, String sortType, CreditType creditType);

    Credit findById(Long id);

    Credit findByUUID(String uuid);

    List<Credit> findAllByUser(User user);

    Optional<Credit> findByCustomerAndMerchant(User customer, User merchant);

    Credit create(Credit credit);

    Credit update(String uuid, Credit updatedCredit);

    void delete(Credit credit);

    Credit findByUUIDAndMerchant(String id, User merchant);

    Credit findSystemCreditByUser(User user);

    void saveAll(List<Credit> credits);
}
