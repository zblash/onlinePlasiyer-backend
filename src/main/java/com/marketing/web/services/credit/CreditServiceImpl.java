package com.marketing.web.services.credit;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Credit;
import com.marketing.web.repositories.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private CreditRepository creditRepository;

    @Override
    public Page<Credit> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
        Page<Credit> resultPage = creditRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Credit findById(Long id) {
        return creditRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Credit not found with id: " + id));
    }

    @Override
    public Credit findByUUID(String uuid) {
        return creditRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Credit not found with id: " + uuid));
    }

    @Override
    public Credit findByUser(Long userId) {
        return creditRepository.findByUser_Id(userId).orElseThrow(() -> new ResourceNotFoundException("Credit not found"));
    }

    @Override
    public Credit create(Credit credit) {
        return creditRepository.save(credit);
    }

    @Override
    public Credit update(String uuid, Credit updatedCredit) {
        Credit credit = findByUUID(uuid);
        credit.setCreditLimit(updatedCredit.getCreditLimit());
        credit.setTotalDebt(updatedCredit.getTotalDebt());
        return creditRepository.save(credit);
    }

    @Override
    public void delete(Credit credit) {
        creditRepository.delete(credit);
    }
}
