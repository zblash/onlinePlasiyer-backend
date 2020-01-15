package com.marketing.web.services.credit;

import com.marketing.web.models.User;
import com.marketing.web.models.UsersCredit;
import org.springframework.data.domain.Page;

public interface UsersCreditService {
    Page<UsersCredit> findAll(int pageNumber, String sortBy, String sortType);

    UsersCredit findById(Long id);

    UsersCredit findByUUID(String uuid);

    UsersCredit findByUser(User user);

    UsersCredit create(UsersCredit usersCredit);

    UsersCredit update(String uuid, UsersCredit updatedUsersCredit);

    void delete(UsersCredit usersCredit);
}
