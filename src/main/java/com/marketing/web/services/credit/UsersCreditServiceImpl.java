package com.marketing.web.services.credit;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.User;
import com.marketing.web.models.UsersCredit;
import com.marketing.web.repositories.UsersCreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsersCreditServiceImpl implements UsersCreditService {

    @Autowired
    private UsersCreditRepository usersCreditRepository;

    @Override
    public Page<UsersCredit> findAll(int pageNumber, String sortBy, String sortType) {
        return null;
    }

    @Override
    public UsersCredit findById(Long id) {
        return usersCreditRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Credit not found with id: " + id));
    }

    @Override
    public UsersCredit findByUUID(String uuid) {
        return usersCreditRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Credit not found with id: " + uuid));
    }

    @Override
    public List<UsersCredit> findAllByUser(User user) {
        return usersCreditRepository.findAllByMerchantOrCustomer(user, user);
    }

    @Override
    public UsersCredit create(UsersCredit usersCredit) {
        return usersCreditRepository.save(usersCredit);
    }

    @Override
    public UsersCredit update(String uuid, UsersCredit updatedUsersCredit) {
        UsersCredit usersCredit = findByUUID(uuid);
        usersCredit.setTotalDebt(updatedUsersCredit.getTotalDebt());
        usersCredit.setCreditLimit(updatedUsersCredit.getTotalDebt());
        usersCredit.setMerchant(updatedUsersCredit.getMerchant());
        usersCredit.setCustomer(updatedUsersCredit.getCustomer());
        return usersCreditRepository.save(usersCredit);
    }

    @Override
    public void delete(UsersCredit usersCredit) {
        usersCreditRepository.delete(usersCredit);
    }

    @Override
    public UsersCredit findByUUIDAndMerchant(String id, User merchant) {
       return usersCreditRepository.findByUuidAndMerchant(UUID.fromString(id), merchant).orElseThrow(() -> new ResourceNotFoundException("Credit not found with id: " + id));
    }
}
