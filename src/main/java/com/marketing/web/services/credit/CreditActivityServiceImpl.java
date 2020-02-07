package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import com.marketing.web.repositories.CreditActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreditActivityServiceImpl implements CreditActivityService {

    @Autowired
    private CreditActivityRepository creditActivityRepository;

    @Override
    public Page<CreditActivity> findAll(int pageNumber, String sortBy, String sortType) {
        return null;
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
        return creditActivityRepository.save(creditActivity);
    }

    @Override
    public CreditActivity update(String uuid, CreditActivity updatedCreditActivity) {
        CreditActivity creditActivity = findByUUID(uuid);
        creditActivity.setCreditActivityType(updatedCreditActivity.getCreditActivityType());
        creditActivity.setPriceValue(updatedCreditActivity.getPriceValue());
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
}
