package com.marketing.web.services.credit;

import com.marketing.web.enums.CreditType;
import com.marketing.web.models.User;
import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;

public interface CreditService {
    Page<Credit> findAllByCreditType(int pageNumber, String sortBy, String sortType, CreditType creditType);

    Credit findById(Long id);

    Credit findByUUID(String uuid);

    Page<Credit> findAllByUserAndCreditType(User user, CreditType creditType, int pageNumber, String sortBy, String sortType);

    Page<Credit> findByCustomerAndMerchant(User customer, User merchant, int pageNumber, String sortBy, String sortType);

    Optional<Credit> findByCustomerAndMerchant(User customer, User merchant);

    Credit create(Credit credit);

    Credit update(String uuid, Credit updatedCredit);

    void delete(Credit credit);

    Credit findByUUIDAndMerchant(String id, User merchant);

    Credit findSystemCreditByUser(User user);

    void saveAll(List<Credit> credits);

    List<Credit> findAllByUsers(User user1, User user2);
}
