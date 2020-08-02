package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Customer;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;
import com.marketing.web.models.Credit;
import com.marketing.web.repositories.CreditRepository;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;

    private final MerchantService merchantService;

    public CreditServiceImpl(CreditRepository creditRepository, MerchantService merchantService) {
        this.creditRepository = creditRepository;
        this.merchantService = merchantService;
    }

    @Override
    public Page<Credit> findAllByCreditType(int pageNumber, String sortBy, String sortType, CreditType creditType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Credit> resultPage = creditRepository.findAllByCreditType(creditType, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Credit> findAllByCustomer(Customer customer, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Credit> resultPage = creditRepository.findAllByCustomer(customer, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Credit> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Credit> resultPage = creditRepository.findAllByMerchant(merchant, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Credit findById(String id) {
        return creditRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",id.toString()));
    }

    @Override
    public Page<Credit> findByCustomerAndMerchant(Customer customer, Merchant merchant, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Credit> resultPage = creditRepository.findAllByCustomerAndMerchant(customer, merchant, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Optional<Credit> findByCustomerAndMerchant(Customer customer, Merchant merchant) {
        return creditRepository.findByCustomerAndMerchant(customer, merchant);
    }

    @Override
    public Optional<Credit> findByCustomer(Customer customer) {
        return creditRepository.findByCustomer(customer);
    }

    @Override
    public Credit create(Credit credit) {
        return creditRepository.save(credit);
    }

    @Override
    public Credit update(String uuid, Credit updatedCredit) {
        Credit credit = findById(uuid);
        credit.setTotalDebt(updatedCredit.getTotalDebt());
        credit.setCreditLimit(updatedCredit.getCreditLimit());
        if (updatedCredit.getCustomer() != null) {
            credit.setCustomer(updatedCredit.getCustomer());
        }
        if (updatedCredit.getMerchant() != null) {
            credit.setMerchant(updatedCredit.getMerchant());
        }
        credit.setCreditType(updatedCredit.getCreditType());
        return creditRepository.save(credit);
    }

    @Override
    public void delete(Credit credit) {
        creditRepository.delete(credit);
    }

    @Override
    public Credit findByUUIDAndMerchant(String id, Merchant merchant) {
       return creditRepository.findByIdAndMerchant(UUID.fromString(id), merchant).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user", id));
    }

    @Override
    public Credit findSystemCreditByCustomer(Customer customer) {
        return creditRepository.findByCustomerAndCreditType(customer, CreditType.SYSTEM_CREDIT).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit", ""));
    }

    @Override
    public void saveAll(List<Credit> credits) {
        creditRepository.saveAll(credits);
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,20, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
