package com.marketing.web.services.invoice;

import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Obligation;
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
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Obligation findById(Long id) {
        return obligationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Obligation not found with id: "+ id));
    }

    @Override
    public Obligation findByUuid(String uuid) {
        return obligationRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Obligation not found with id: "+ uuid));
    }

    @Override
    public Page<Obligation> findAllByUser(User user, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Obligation> resultPage = obligationRepository.findAllByUser(user,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public ReadableTotalObligation getTotalObligationByUser(User user) {
        List<Obligation> obligations = obligationRepository.findAllByUserOrderByIdDesc(user);
        ReadableTotalObligation readableTotalObligation = new ReadableTotalObligation();
        readableTotalObligation.setId(user.getUuid().toString()+user.getId().toString());
        readableTotalObligation.setTotalDebts(obligations.stream().flatMapToDouble(obligation -> DoubleStream.of(obligation.getDebt())).sum());
        readableTotalObligation.setTotalReceivables(obligations.stream().flatMapToDouble(obligation -> DoubleStream.of(obligation.getReceivable())).sum());
        return readableTotalObligation;
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
