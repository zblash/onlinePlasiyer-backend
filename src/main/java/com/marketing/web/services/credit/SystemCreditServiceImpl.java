package com.marketing.web.services.credit;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.SystemCredit;
import com.marketing.web.repositories.SystemCreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SystemCreditServiceImpl implements SystemCreditService {

    @Autowired
    private SystemCreditRepository systemCreditRepository;

    @Override
    public Page<SystemCredit> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
        Page<SystemCredit> resultPage = systemCreditRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public SystemCredit findById(Long id) {
        return systemCreditRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit", id.toString()));
    }

    @Override
    public SystemCredit findByUUID(String uuid) {
        return systemCreditRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit", uuid));
    }

    @Override
    public SystemCredit findByUser(Long userId) {
        return systemCreditRepository.findByUser_Id(userId).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit",""));
    }

    @Override
    public SystemCredit create(SystemCredit systemCredit) {
        return systemCreditRepository.save(systemCredit);
    }

    @Override
    public SystemCredit update(String uuid, SystemCredit updatedSystemCredit) {
        SystemCredit systemCredit = findByUUID(uuid);
        systemCredit.setCreditLimit(updatedSystemCredit.getCreditLimit());
        systemCredit.setTotalDebt(updatedSystemCredit.getTotalDebt());
        return systemCreditRepository.save(systemCredit);
    }

    @Override
    public void delete(SystemCredit systemCredit) {
        systemCreditRepository.delete(systemCredit);
    }
}
