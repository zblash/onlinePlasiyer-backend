package com.marketing.web.services.invoice;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.repositories.ObligationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.DoubleStream;

@Service
public class ObligationServiceImpl implements ObligationService {

    @Autowired
    private ObligationRepository obligationRepository;

    private Logger logger = LoggerFactory.getLogger(ObligationServiceImpl.class);


    @Override
    public Page<Obligation> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Obligation> resultPage = obligationRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Obligation findById(Long id) {
        return obligationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"obligation",id.toString()));
    }

    @Override
    public Obligation findByUuid(String uuid) {
        return obligationRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"obligation", uuid));
    }

    @Override
    public Obligation findByUser(User user) {
        return obligationRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"obligation", ""));

    }

    @Override
    public Obligation create(Obligation obligation) {
        return obligationRepository.save(obligation);
    }

    @Override
    public List<Obligation> createAll(List<Obligation> obligations) {
        return obligationRepository.saveAll(obligations);
    }

    @Override
    public Obligation update(String uuid, Obligation updatedObligation) {
        Obligation obligation = findByUuid(uuid);
        obligation.setUser(updatedObligation.getUser());
        obligation.setReceivable(updatedObligation.getReceivable());
        obligation.setDebt(updatedObligation.getDebt());
        return obligationRepository.save(obligation);
    }

    @Override
    public void delete(Obligation obligation) {

    }
    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
