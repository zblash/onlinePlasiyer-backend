package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CreditActivityRepository;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CreditActivityServiceImpl implements CreditActivityService {

    @Autowired
    private CreditActivityRepository creditActivityRepository;

    @Override
    public Page<CreditActivity> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<CreditActivity> resultPage = creditActivityRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<CreditActivity> findAllByUser(User user, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<CreditActivity> resultPage = creditActivityRepository.findAllByCustomerOrMerchant(user, user, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public CreditActivity findById(Long id) {
        return creditActivityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",id.toString()));
    }

    @Override
    public CreditActivity findByUUID(String uuid) {
        return creditActivityRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",uuid));

    }

    @Override
    public List<CreditActivity> findAllByCredit(Credit credit) {
        return creditActivityRepository.findAllByCredit(credit);
    }

    @Override
    public List<CreditActivity> findAllByOrder(Order order) {
        return creditActivityRepository.findAllByOrder(order);
    }

    @Override
    public CreditActivity create(CreditActivity creditActivity) {
        creditActivity.setDate(new Date());
        return creditActivityRepository.save(creditActivity);
    }

    @Override
    public CreditActivity update(String uuid, CreditActivity updatedCreditActivity) {
        CreditActivity creditActivity = findByUUID(uuid);
        creditActivity.setCreditActivityType(updatedCreditActivity.getCreditActivityType());
        creditActivity.setPriceValue(updatedCreditActivity.getPriceValue());
        creditActivity.setDate(new Date());
        return creditActivity;
    }

    @Override
    public void delete(CreditActivity creditActivity) {
        creditActivityRepository.delete(creditActivity);
    }

    @Override
    public void deleteByOrder(Order order) {
        creditActivityRepository.deleteByOrder(order);
    }

    @Override
    public void saveAll(List<CreditActivity> creditActivities) {
        creditActivityRepository.saveAll(creditActivities);
    }

    @Override
    public Page<CreditActivity> findAllByUsers(User user1, User user2, Integer pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        RoleType roleType = UserMapper.roleToRoleType(user1.getRole());
        Page<CreditActivity> resultPage = creditActivityRepository.findAllByCustomerAndMerchant(RoleType.MERCHANT.equals(roleType) ? user2 : user1, RoleType.MERCHANT.equals(roleType) ? user1 : user2, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
