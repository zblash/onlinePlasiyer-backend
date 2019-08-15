package com.marketing.web.services;

import com.marketing.web.models.User;

import java.util.List;

public interface IUserService {

    User findByUserName(String userName);

    List<User> findAll();

    User findById(Long id);

    User create(User user);

    User update(User user);

    void delete(User user);
}
