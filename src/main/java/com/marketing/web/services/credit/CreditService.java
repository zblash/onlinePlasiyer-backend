package com.marketing.web.services.credit;

import com.marketing.web.enums.CreditType;
import com.marketing.web.models.Customer;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;
import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;

public interface CreditService {
    Page<Credit> findAllByCreditType(int pageNumber, String sortBy, String sortType, CreditType creditType);

    Page<Credit> findAllByCustomer(Customer customer, int pageNumber, String sortBy, String sortType);

    Page<Credit> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType);

    Credit findById(String id);

    Page<Credit> findByCustomerAndMerchant(Customer customer, Merchant merchant, int pageNumber, String sortBy, String sortType);

    Optional<Credit> findByCustomerAndMerchant(Customer customer, Merchant merchant);

    Optional<Credit> findByCustomer(Customer customer);

    Credit create(Credit credit);

    Credit update(String id, Credit updatedCredit);

    void delete(Credit credit);

    Credit findByUUIDAndMerchant(String id, Merchant merchant);

    Credit findSystemCreditByCustomer(Customer customer);

    void saveAll(List<Credit> credits);

}
