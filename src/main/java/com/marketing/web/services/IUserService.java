package com.marketing.web.services;

import com.marketing.web.models.RoleType;
import com.marketing.web.models.User;

import java.util.List;

public interface IUserService {

    User findByUserName(String userName);

    List<User> findAll();

    List<User> findAllByStatus(boolean status);

    User findById(Long id);

    User create(User user, RoleType roleType);

    User update(User user, User updatedUser);

    void delete(User user);
}
