package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.User;
import com.marketing.web.models.Credit;
import com.marketing.web.repositories.CreditRepository;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private CreditRepository creditRepository;

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
    public Credit findById(Long id) {
        return creditRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",id.toString()));
    }

    @Override
    public Credit findByUUID(String uuid) {
        return creditRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user", uuid));
    }

    @Override
    public Page<Credit> findAllByUserAndCreditType(User user, CreditType creditType, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Credit> resultPage = null;
        RoleType roleType = UserMapper.roleToRoleType(user.getRole());
        if (roleType.equals(RoleType.MERCHANT)) {
            resultPage = creditRepository.findAllByCreditTypeAndMerchant(creditType, user, pageRequest);
        } else {
            resultPage = creditRepository.findAllByCreditTypeAndCustomer(creditType, user, pageRequest);
        }

        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Credit> findByCustomerAndMerchant(User customer, User merchant, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Credit> resultPage = creditRepository.findAllByCustomerAndMerchant(customer, merchant, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Optional<Credit> findByCustomerAndMerchant(User customer, User merchant) {
        return creditRepository.findByCustomerAndMerchant(customer, merchant);
    }

    @Override
    public Credit create(Credit credit) {
        return creditRepository.save(credit);
    }

    @Override
    public Credit update(String uuid, Credit updatedCredit) {
        Credit credit = findByUUID(uuid);
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
    public Credit findByUUIDAndMerchant(String id, User merchant) {
       return creditRepository.findByUuidAndMerchant(UUID.fromString(id), merchant).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user", id));
    }

    @Override
    public Credit findSystemCreditByUser(User user) {
        return creditRepository.findByCustomerAndCreditType(user, CreditType.SYSTEM_CREDIT).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit", ""));
    }

    @Override
    public void saveAll(List<Credit> credits) {
        creditRepository.saveAll(credits);
    }

    @Override
    public List<Credit> findAllByUsers(User user1, User user2) {
        RoleType roleType = UserMapper.roleToRoleType(user1.getRole());
        return creditRepository.findAllByMerchantAndCustomer(RoleType.MERCHANT.equals(roleType) ? user1 : user2,RoleType.MERCHANT.equals(roleType) ? user2 : user1);
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,20, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
