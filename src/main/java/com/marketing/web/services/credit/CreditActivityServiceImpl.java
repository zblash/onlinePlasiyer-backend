package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CreditActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CreditActivityServiceImpl implements CreditActivityService {

    private final CreditActivityRepository creditActivityRepository;

    public CreditActivityServiceImpl(CreditActivityRepository creditActivityRepository) {
        this.creditActivityRepository = creditActivityRepository;
    }

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
    public Page<CreditActivity> findAllBySpecification(Specification<CreditActivity> specification, Integer pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<CreditActivity> resultPage = creditActivityRepository.findAll(specification, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public CreditActivity findById(String id) {
        return creditActivityRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",id.toString()));
    }

    @Override
    public List<CreditActivity> findAllByOrder(Order order) {
        return creditActivityRepository.findAllByOrder(order);
    }

    @Override
    public CreditActivity create(CreditActivity creditActivity) {
        creditActivity.setDate(LocalDate.now());
        return creditActivityRepository.save(creditActivity);
    }

    @Override
    public CreditActivity update(String id, CreditActivity updatedCreditActivity) {
        CreditActivity creditActivity = findById(id);
        creditActivity.setCreditActivityType(updatedCreditActivity.getCreditActivityType());
        creditActivity.setPriceValue(updatedCreditActivity.getPriceValue());
        creditActivity.setDate(LocalDate.now());
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

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
