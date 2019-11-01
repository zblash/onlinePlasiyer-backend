package com.marketing.web.services.invoice;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import com.marketing.web.repositories.ObligationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ObligationServiceImpl implements ObligationService {

    @Autowired
    private ObligationRepository obligationRepository;

    private Logger logger = LoggerFactory.getLogger(ObligationServiceImpl.class);


    @Override
    public Page<Obligation> findAll(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15);
        Page<Obligation> resultPage = obligationRepository.findAllByOrderByIdDesc(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Obligation findById(Long id) {
        return obligationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Obligation not found with id: "+ id));
    }

    @Override
    public Page<Obligation> findAllByUser(User user, int pageNumber) {
        return null;
    }

    @Override
    public Obligation create(Obligation obligation) {
        return null;
    }

    @Override
    public Obligation update(Long id, Obligation updatedObligation) {
        return null;
    }

    @Override
    public void delete(Obligation obligation) {

    }
}
