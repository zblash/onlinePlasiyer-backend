package com.marketing.web.services.invoice;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.ObligationActivity;
import com.marketing.web.repositories.ObligationActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ObligationActivityServiceImpl implements ObligationActivityService {

    @Autowired
    private ObligationActivityRepository obligationActivityRepository;

    @Override
    public Page<ObligationActivity> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ObligationActivity> resultPage = obligationActivityRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<ObligationActivity> findAllByObligation(Obligation obligation, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ObligationActivity> resultPage = obligationActivityRepository.findAllByObligation(obligation, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public ObligationActivity findById(Long id) {
        return obligationActivityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"obligation", id.toString()));
    }

    @Override
    public ObligationActivity findByUuid(String uuid) {
        return obligationActivityRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"obligation", uuid));
    }

    @Override
    public ObligationActivity create(ObligationActivity obligationActivity) {
        return obligationActivityRepository.save(obligationActivity);
    }

    @Override
    public ObligationActivity update(String uuid, ObligationActivity updatedObligationActivity) {
        ObligationActivity obligationActivity = findByUuid(uuid);
        obligationActivity.setPriceValue(updatedObligationActivity.getPriceValue());
        obligationActivity.setObligation(updatedObligationActivity.getObligation());
        return obligationActivity;
    }

    @Override
    public List<ObligationActivity> saveAll(List<ObligationActivity> obligationActivities) {
        return obligationActivityRepository.saveAll(obligationActivities);
    }

    @Override
    public void deleteAll(List<ObligationActivity> obligationActivities) {
        obligationActivityRepository.deleteAll(obligationActivities);
    }

    @Override
    public void delete(ObligationActivity obligationActivity) {
        obligationActivityRepository.delete(obligationActivity);
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
