package com.marketing.web.services.credit;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.User;
import com.marketing.web.models.UsersCredit;
import com.marketing.web.repositories.UsersCreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.UUID;

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
    public UsersCredit findByUser(User user) {
        return null;
    }

    @Override
    public UsersCredit create(UsersCredit usersCredit) {
        return null;
    }

    @Override
    public UsersCredit update(String uuid, UsersCredit updatedUsersCredit) {
        return null;
    }

    @Override
    public void delete(UsersCredit usersCredit) {

    }
}
